# AGENTS.md - KMP Shared Module Architecture & Guidelines

## Tech Stack & Versions
- **Language**: Kotlin 2.x (Multiplatform)
- **Serialization**: `kotlinx.serialization`
- **Networking**: Ktor Client (`io.ktor:ktor-client-*`)
- **Database**: Room KMP (`androidx.room`) with SQLite Bundled Driver
- **Dependency Injection**: Koin (`io.insert-koin:koin-core`)

## Architecture & Layers
- **Data Layer**: Network API services (`KmmService`, `DataNetwork`), Local database (`AppDataBase`, DAOs, Entities), and Data Source implementations.
- **Domain / Use Cases**: `DataUseCase` acts as the single source of truth for business logic and data orchestration between network and database.
- **Models**:
  - Network responses (`*Response`)
  - Database entities (`*Entity`)
  - Shared domain models (`*Model`)
  - Mappers (`Mappers.kt`, `toModel()`, `toEntity()`)

## Conventions & Rules
- Do not mix platform-specific Android/iOS code in `commonMain` unless wrapped in `expect`/`actual`.
- Keep API response models strictly serialized with `@Serializable` and `@SerialName`.
- All database entities must be annotated with `@Entity` and managed via Room `@Database`.
