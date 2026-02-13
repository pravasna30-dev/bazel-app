# ECMonorepo — Local Dev Setup

## Prerequisites

- [Bazel](https://bazel.build/) 9.0.0 (managed via `.bazelversion`)
- Java 21
- Git

## Workspace Layout

All repositories must be cloned as siblings under the same parent directory:

```
parent-directory/
  ecmonorepo/           # This repository (consumer)
  bazel-monorepo/       # Producer: Bazel module integration
  dep-library/          # Producer: Source copy integration
  mid-maven-lib/        # Producer: Source copy integration
```

### Clone All Repositories

```bash
git clone https://github.com/pravasna30-dev/ecmonorepo.git
git clone https://github.com/pravasna30-dev/bazel-monorepo.git
git clone https://github.com/pravasna30-dev/dep-library.git
git clone https://github.com/pravasna30-dev/mid-maven-lib.git
```

## Producers

| Producer | Repository | Branch | Build System | Integration Type | Bazel Target |
|----------|-----------|--------|-------------|-----------------|-------------|
| composite-monorepo | [bazel-monorepo](https://github.com/pravasna30-dev/bazel-monorepo) | main | Bazel | `local_path_override` | `@composite-monorepo//low-level-1:LowLevelOne` |
| dep-library | [dep-library](https://github.com/pravasna30-dev/dep-library) | main | Gradle/Maven | Source copy | `//library:library` |
| mid-maven-lib | [mid-maven-lib](https://github.com/pravasna30-dev/mid-maven-lib) | main | Maven | Source copy | `//midlib:library` |

## How It Works

### Bazel Module Integration (composite-monorepo)

`MODULE.bazel` declares a dependency on the `composite-monorepo` module and uses `local_path_override` to resolve it from the sibling directory:

```python
bazel_dep(name = "composite-monorepo", version = "1.0.0")
local_path_override(
    module_name = "composite-monorepo",
    path = "../bazel-monorepo",
)
```

The `BUILD` file references the target directly:

```python
deps = [
    "@composite-monorepo//low-level-1:LowLevelOne",
]
```

Bazel builds `low-level-1` from source. Changes in `bazel-monorepo` are picked up immediately on the next build.

### Source Copy Integration (dep-library, mid-maven-lib)

For producers that use Gradle or Maven, their Java sources are copied into local Bazel packages:

- `dep-library/src/main/java/` → `library/src/main/java/`
- `mid-maven-lib/src/main/java/` → `midlib/src/main/java/`

Each package has a `BUILD` file with a `java_library` target:

```python
java_library(
    name = "library",
    srcs = glob(["src/main/java/**/*.java"]),
    visibility = ["//visibility:public"],
)
```

The root `BUILD` references them as native Bazel targets:

```python
deps = [
    "//library:library",
    "//midlib:library",
]
```

## Build and Run

```bash
cd ecmonorepo
bazel run //:app
```

### Expected Output

```
I love bazel
Low-level-1
All users: [User{id=1, email='john.doe@example.com', name='John Doe'}, User{id=2, email='jane.doe@example.com', name='Jane Doe'}]
I am a MID level library
```

## Dependency Graph

See the [interactive dependency graph](dependency-graph.html) for a visual representation of all producer-consumer relationships.
