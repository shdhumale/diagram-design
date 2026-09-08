# Repository AI Instruction

## Architecture

Treat the repository source code and approved documentation as the source of truth.

Do not invent services, APIs, databases, technologies, dependencies or deployment environments.

Clearly label assumptions and unresolved questions.

## Diagram generation

Use the diagram-design Agent Skill for architecture, data-flow, sequence, dependency, deployment, database, process and SDLC diagrams.

Write generated diagrams under, docs/diagrams/.

Use self-contained HTML with embedded SVG.

Prefer static output unless motion is explicitly requested.

Keep diagrams readable and minimize unnecessary nodes.

Include the diagram purpose, scope, assumptions and source files.

Preserve existing branding and terminology.

Do not include credentials, customer data, production endpoints or secrets.

## Validation

Validate component names against the repository.

Validate API relationships against controllers, clients and configuration.

Validate database entities against migrations or schema definitions.

Validate deployment components against Docker, Helm or Kubernetes files.

Mark relationships as "unverified" when evidence is insufficient.