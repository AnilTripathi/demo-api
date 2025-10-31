# Task Management Schema Implementation

## Overview

This document describes the implementation of a comprehensive task management schema for the MyHealth API, including Flyway migration, JPA entities, and Spring Data repositories.

## Database Schema

### Migration File
- **Location**: `src/main/resources/db/migration/V3__task_schema.sql`
- **Database**: PostgreSQL with UUID and JSONB support
- **Extensions**: Uses `uuid-ossp` extension for UUID generation

### Tables Created

#### Lookup Tables
- **priorities**: Task priority levels (1-5: Lowest to Highest)
- **statuses**: Task status types (1-5: Backlog to Done)

#### Core Tables
- **tasks**: Main task entity with full task lifecycle support
- **labels**: Reusable labels for task categorization
- **task_labels**: Many-to-many relationship between tasks and labels

#### Relationship Tables
- **task_dependencies**: Task dependency relationships with dependency types (FS, SS, FF, SF)
- **checklists**: Task checklists for breaking down work
- **checklist_items**: Individual items within checklists

#### Activity Tables
- **comments**: Task comments with markdown support
- **attachments**: File attachments linked to tasks
- **reminders**: Task reminders with multiple notification channels

### Key Features

#### JSONB Support
- `tasks.extras`: Custom metadata storage
- `reminders.payload`: Flexible reminder configuration

#### Optimistic Locking
- `tasks.version`: Prevents concurrent modification conflicts

#### Soft Delete
- `tasks.deleted_at`: Soft delete support for data retention

#### Full-Text Search
- GIN index on task title and description for efficient text search

#### Cascade Deletes
- All child entities cascade delete when parent task is removed

## JPA Entity Implementation

### Entity Classes

#### Lookup Entities
- `Priority`: Task priority levels
- `Status`: Task status with completion flag

#### Core Entities
- `Task`: Main task entity with all relationships
- `Label`: Reusable task labels
- `TaskDependency`: Task dependencies with composite key
- `TaskDependencyId`: Composite key class for dependencies

#### Child Entities
- `Checklist`: Task checklists
- `ChecklistItem`: Individual checklist items
- `Comment`: Task comments
- `Attachment`: File attachments
- `Reminder`: Task reminders with channel enum

### Key Mapping Features

#### JSONB Mapping
```java
@JdbcTypeCode(SqlTypes.JSON)
@Column(name = "extras", columnDefinition = "jsonb")
private Map<String, Object> extras;
```

#### Optimistic Locking
```java
@Version
@Column(name = "version")
private Integer version;
```

#### Audit Fields
```java
@PrePersist
protected void onCreate() {
    createdAt = ZonedDateTime.now();
    updatedAt = ZonedDateTime.now();
}
```

#### Cascade Relationships
```java
@OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
private Set<Checklist> checklists;
```

## Repository Implementation

### Repository Classes
- `TaskRepository`: Main task operations with custom queries
- `LabelRepository`: Label management
- `TaskDependencyRepository`: Dependency relationship management
- `ChecklistRepository`, `ChecklistItemRepository`: Checklist operations
- `CommentRepository`, `AttachmentRepository`, `ReminderRepository`: Activity tracking
- `PriorityRepository`, `StatusRepository`: Lookup data access

### Custom Query Examples

#### Due Tasks Query
```java
@Query("SELECT t FROM Task t WHERE t.dueAt <= :dueDate AND t.deletedAt IS NULL AND t.isArchived = false")
List<Task> findTasksDueBefore(@Param("dueDate") ZonedDateTime dueDate);
```

#### Due Reminders Query
```java
@Query("SELECT r FROM Reminder r WHERE r.remindAt <= :currentTime ORDER BY r.remindAt")
List<Reminder> findDueReminders(@Param("currentTime") ZonedDateTime currentTime);
```

## Running the Migration

### Prerequisites
- PostgreSQL 13+ running
- Application configured with database connection
- Flyway enabled in Spring Boot configuration

### Migration Execution
```bash
# Run with development profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Or run tests (uses H2, limited JSONB support)
./mvnw test
```

### Verification
```sql
-- Check tables were created
SELECT table_name FROM information_schema.tables 
WHERE table_schema = 'public' AND table_name LIKE '%task%';

-- Verify lookup data
SELECT * FROM priorities;
SELECT * FROM statuses;
```

## Testing

### Repository Tests
- **TaskRepositoryTest**: Validates CRUD operations, JSONB mapping, cascade deletes
- **TaskDependencyRepositoryTest**: Tests composite key operations and cascades

### Test Coverage
- Entity mapping validation
- JSONB roundtrip serialization
- Cascade delete behavior
- Custom query functionality
- Constraint validation

### Running Tests
```bash
# Run all repository tests
./mvnw test -Dtest="*RepositoryTest"

# Run specific task tests
./mvnw test -Dtest="TaskRepositoryTest"
```

## Usage Examples

### Creating a Task with Extras
```java
Task task = new Task();
task.setTitle("Implement feature X");
task.setStatus(todoStatus);
task.setPriority(highPriority);

Map<String, Object> extras = new HashMap<>();
extras.put("estimatedHours", 8);
extras.put("complexity", "medium");
task.setExtras(extras);

taskRepository.save(task);
```

### Adding Task Dependencies
```java
TaskDependency dependency = new TaskDependency();
dependency.setTaskId(task1.getId());
dependency.setDependsOnId(task2.getId());
dependency.setDepType(DependencyType.FS); // Finish-to-Start
taskDependencyRepository.save(dependency);
```

### Creating Reminders
```java
Reminder reminder = new Reminder();
reminder.setTask(task);
reminder.setRemindAt(ZonedDateTime.now().plusHours(1));
reminder.setChannel(ReminderChannel.email);

Map<String, Object> payload = new HashMap<>();
payload.put("recipient", "user@example.com");
payload.put("template", "task_due");
reminder.setPayload(payload);

reminderRepository.save(reminder);
```

## Future Enhancements

### API Endpoints (TODO)
- REST endpoints for task CRUD operations
- Task search and filtering
- Bulk operations
- Task analytics and reporting

### Additional Features (TODO)
- Task templates
- Time tracking integration
- Notification service integration
- Task import/export
- Advanced search with Elasticsearch

## Troubleshooting

### Common Issues

#### JSONB Not Supported (H2 Testing)
- H2 database doesn't fully support JSONB
- Use PostgreSQL for integration tests or mock JSONB fields

#### UUID Generation Issues
- Ensure `uuid-ossp` extension is installed
- Verify PostgreSQL version supports UUID generation

#### Cascade Delete Not Working
- Check entity relationships are properly mapped
- Verify `cascade = CascadeType.ALL` is set correctly

### Migration Rollback
```sql
-- Manual rollback (if needed)
DROP TABLE IF EXISTS reminders CASCADE;
DROP TABLE IF EXISTS attachments CASCADE;
DROP TABLE IF EXISTS comments CASCADE;
DROP TABLE IF EXISTS checklist_items CASCADE;
DROP TABLE IF EXISTS checklists CASCADE;
DROP TABLE IF EXISTS task_dependencies CASCADE;
DROP TABLE IF EXISTS task_labels CASCADE;
DROP TABLE IF EXISTS labels CASCADE;
DROP TABLE IF EXISTS tasks CASCADE;
DROP TABLE IF EXISTS statuses CASCADE;
DROP TABLE IF EXISTS priorities CASCADE;
```

## Performance Considerations

### Indexes Created
- `idx_tasks_parent`: Parent task lookups
- `idx_tasks_status`: Status filtering
- `idx_tasks_due`: Due date queries (filtered)
- `idx_tasks_search`: Full-text search (GIN)
- `idx_comments_task`: Comment retrieval
- `idx_reminders_due`: Due reminder processing

### Query Optimization Tips
- Use indexed fields in WHERE clauses
- Leverage the GIN index for text search
- Consider pagination for large result sets
- Use projection queries for read-only operations