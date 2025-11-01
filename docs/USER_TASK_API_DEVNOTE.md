# User Task API - Development Notes

## Overview

The User Task API provides comprehensive task management functionality for authenticated users under the `/api/user/task` endpoint. The implementation follows clean architecture principles with optimized data access using projections.

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/user/task` | List user tasks with filters and pagination |
| GET | `/api/user/task/{id}` | Get task details |
| POST | `/api/user/task` | Create new task |
| PUT | `/api/user/task/{id}` | Update existing task |
| DELETE | `/api/user/task/{id}` | Soft delete task |
| PATCH | `/api/user/task/{id}/status` | Change task status |

## Projections Used

### UserTaskListProjection
Used for listing tasks with minimal data transfer:
- `id`, `title`, `statusId`, `statusName`, `priorityId`, `priorityName`
- `dueAt`, `estimateMinutes`, `createdAt`, `updatedAt`

### UserTaskDetailProjection  
Used for detailed task view with complete information:
- All fields from list projection plus:
- `descriptionMd`, `spentMinutes`, `completedAt`

## Supported Filters

The list endpoint supports the following filters:
- **status**: Filter by status ID (1=Backlog, 2=Todo, 3=In Progress, 4=Blocked, 5=Done)
- **q**: Search query for title and description (case-insensitive)
- **fromDue**: Filter tasks due from this date
- **toDue**: Filter tasks due until this date
- **page**: Page number (0-based, default: 0)
- **size**: Page size (default: 20)
- **sort**: Sort field and direction (default: "createdAt,desc")

## Status Workflow

### Status Transitions
1. **Backlog (1)** → Todo, In Progress
2. **Todo (2)** → In Progress, Blocked, Done
3. **In Progress (3)** → Todo, Blocked, Done
4. **Blocked (4)** → Todo, In Progress, Done
5. **Done (5)** → Any status (with validation)

### Business Rules
- Moving to Done (5) sets `completedAt` timestamp
- Moving away from Done clears `completedAt`
- Invalid transitions return 409 Conflict (e.g., Done → Backlog directly)

## Security & Ownership

- All endpoints require JWT authentication
- Users can only access their own tasks (scoped by `accountId`)
- User ID extracted from JWT token in SecurityContext
- Soft delete preserves data integrity

## Performance Optimizations

### Database Queries
- Interface-based projections reduce data transfer
- Indexed fields: `account_id`, `status_id`, `due_at`, `deleted_at`
- JPQL queries with JOIN FETCH for required associations
- Pagination prevents large result sets

### Query Examples
```sql
-- List query with projections
SELECT t.id, t.title, s.name as statusName, p.name as priorityName, ...
FROM tasks t 
JOIN statuses s ON t.status_id = s.id 
JOIN priorities p ON t.priority_id = p.id
WHERE t.account_id = ? AND t.deleted_at IS NULL

-- Soft delete
UPDATE tasks SET deleted_at = ? WHERE id = ? AND account_id = ?
```

## Validation Rules

### Create/Update Requests
- **title**: Required, max 255 characters
- **descriptionMd**: Optional, max 2000 characters  
- **priorityId**: Must be 1-5, defaults to 3 (Medium)
- **dueAt**: Optional ISO 8601 datetime
- **estimateMinutes**: Optional positive integer

### Status Change
- **statusId**: Required, must be 1-5
- Validates business rules for transitions

## Error Handling

### HTTP Status Codes
- **200**: Success (GET, PUT, PATCH)
- **201**: Created (POST)
- **204**: No Content (DELETE)
- **400**: Bad Request (validation errors)
- **401**: Unauthorized (missing/invalid JWT)
- **404**: Not Found (task doesn't exist or not owned)
- **409**: Conflict (invalid status transition)

### Error Response Format
```json
{
  "timestamp": "2024-01-15T10:30:00Z",
  "status": 400,
  "path": "/api/user/task",
  "message": "Validation failed",
  "errors": ["title: Title is required"]
}
```

## Testing Strategy

### Unit Tests
- Service layer business logic
- Status transition validation
- Mapping between entities and DTOs
- Error scenarios

### Controller Tests
- HTTP status codes
- Request/response serialization
- Authentication requirements
- Validation error handling

### Repository Tests
- Projection queries
- Filtering and pagination
- User ownership scoping

## Future Enhancements

### Potential Improvements
- Task dependencies and subtasks
- File attachments
- Task templates
- Bulk operations
- Advanced search with Elasticsearch
- Real-time notifications
- Task analytics and reporting

### Performance Considerations
- Consider caching for frequently accessed data
- Database connection pooling optimization
- Async processing for heavy operations
- Rate limiting for API endpoints

## Dependencies

### Required Libraries
- Spring Boot Starter Web
- Spring Boot Starter Data JPA
- Spring Boot Starter Validation
- Spring Boot Starter Security
- SpringDoc OpenAPI (Swagger)
- Lombok

### Database Requirements
- PostgreSQL with UUID support
- Proper indexes on filtered columns
- Foreign key constraints for data integrity