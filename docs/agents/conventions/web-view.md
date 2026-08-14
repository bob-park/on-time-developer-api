---
title: Web View Conventions
scope: src/main/java/**/*.java
applies_to: writing a server-rendered (Thymeleaf) view controller or view DTO
related:
  - ../architecture.md
  - ../structure-dir.md
  - ./web-api.md
  - ../security.md
---

# Web View Conventions

> Server-rendered `@Controller` classes in `adapter.in.web.view`, PRG flow, Form POJO vs. view-model record. Read when writing a server-rendered view controller.

Server-rendered (Thymeleaf) controllers are a distinct inbound adapter from the REST API. They live
in their own package, use `@Controller` (not `@RestController`), and return template names. For REST
controllers, request/response DTOs, and versioning, see [Web API](./web-api.md) instead — those rules
do **not** apply to views.

## Location & Naming

- View controllers live in `{module}.adapter.in.web.view`, the sibling of the REST API package
  `{module}.adapter.in.web.api` ([Directory Structure](../structure-dir.md)).
- Class name is `<Feature>View` — `LoginView`, `SignUpView`, `CodeView`. Contrast the REST API
  suffix `<Resource>ApiV{n}` ([Web API](./web-api.md)).
- **Views are not versioned.** There is no `v1` package segment and no `V{n}` class/DTO suffix —
  server-rendered pages are not a versioned public contract the way the REST API is.
- Request paths are plain human URLs with **no** `api`/version prefix and no leading slash
  (`"login"`, `"signup"`, `"codes"`). Handler methods are named by business verb (`login`, `signup`,
  `register`), not `getX`/`postX`.

## Controller Shape

Class-level: `@RequiredArgsConstructor` + `@Controller` + `@RequestMapping(path = "…")`, adding
`@Slf4j` only when the class actually logs. Dependencies (application-layer `provided` ports) are
injected `private final`. Each handler takes a Spring `Model`, populates it with
`model.addAttribute(...)`, and returns a `String` that is **either** a Thymeleaf template name
(mapping 1:1 to a file under `src/main/resources/templates/`) **or** a `"redirect:/…"` string.

Views never return `@ResponseBody`, typed objects, or `PagedModel`, and never carry `@ResponseStatus`
or `@RequestBody`/`@Valid` (those belong to the REST API). Cross-cutting security wiring stays in the
global security config — see [Security & Auth](../security.md); the view adapter only reads the
resolved principal it is handed.

```java
@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping(path = "codes")
public class CodeView {

    private final CodeQuery query;
    private final CodeRegister register;

    private static final Validator VALIDATOR = new CodeFormValidator();

    @GetMapping(path = "")
    public String list(Model model) {
        model.addAttribute("codes",
            query.getAll().stream().map(CodeItemModel::from).toList());
        return "codes/list";
    }

    @GetMapping(path = "new")
    public String form(Model model) {
        model.addAttribute("form", new CodeForm());
        return "codes/form";
    }

    @PostMapping(path = "")
    public String register(@ModelAttribute("form") CodeForm form,
                           BindingResult bindingResult,
                           Model model) {

        VALIDATOR.validate(form, bindingResult);
        if (bindingResult.hasErrors()) {
            return "codes/form";   // re-render with field errors
        }

        register.register(
            CodeRegisterCommand.builder()
                .name(form.getName().trim())
                .displayName(form.getDisplayName().trim())
                .build());

        return "redirect:/codes";  // Post/Redirect/Get on success
    }
}
```

## Post/Redirect/Get for Form Submits

Form flows follow PRG: a `GET` renders the form template, the `POST` binds the form, validates it,
and — on any error — **re-renders the same template** with the errors in `BindingResult`; on success
it invokes the application-layer port and returns a `"redirect:/…"`. Never render a mutation's result
inline. Unlike the REST API (bean-validation → RFC 9457 `ProblemDetail`), a view surfaces validation
failures by pushing them into `BindingResult` and re-rendering; error codes are dotted
message-bundle keys (`"<feature>.error.<field>.<reason>"`).

## View DTOs in `view/dto`

View DTOs live in a nested `{module}.adapter.in.web.view.dto` package and come in two flavors:

- **Inbound form beans** — mutable POJOs bound via `@ModelAttribute`, named `<Feature>Form`
  (`CodeForm`, `SignUpForm`). Lombok `@Getter @Setter @NoArgsConstructor`; no Jakarta validation
  annotations (validation lives in the separate `Validator`, below). Exclude secrets from
  `@ToString` (e.g. `@ToString(exclude = {"password"})`).

```java
@Getter
@Setter
@NoArgsConstructor
public class CodeForm {
    private String name;
    private String displayName;
}
```

- **Outbound view-model records** — immutable render models named `<Feature>Model`, an immutable
  `record` with `@Builder` and a static `from(...)` factory mapping the application-layer `*Result`.
  Identifier fields are `String` (via `String.valueOf(...)`), the same ID rule as
  [Web API](./web-api.md). Use a single consistent suffix (`…Model`) for view-models across the
  codebase.

```java
@Builder
public record CodeItemModel(String id,
                            String name,
                            String displayName) {

    public static CodeItemModel from(CodeResult result) {
        return CodeItemModel.builder()
            .id(String.valueOf(result.id()))
            .name(result.name())
            .displayName(result.displayName())
            .build();
    }
}
```

## Validation via a Dedicated `Validator`

Form validation lives in a separate `<Form>Validator` class implementing
`org.springframework.validation.Validator` — not in annotations on the form bean. The controller
runs it against the bound form and the `BindingResult`, rejecting fields with message-key codes
(`bindingResult.rejectValue("name", "codes.error.name.required")`). Keep policy (patterns, length
limits, uniqueness checks) inside the validator so the form bean stays a plain data holder.
