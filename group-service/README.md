# Group Service (group-service:8083)

## Overview
The Group Service is responsible for managing study groups in the platform. It handles CRUD operations for study groups, admin approval processes, join request management, and member management.

## Database Schema

### Tables

#### groups
- **id** (BIGSERIAL PRIMARY KEY): Unique identifier for the group
- **name** (VARCHAR(255) NOT NULL): Name of the study group
- **subject** (VARCHAR(255) NOT NULL): Subject/Topic of the group
- **description** (TEXT NOT NULL): Detailed description of the group
- **creator_id** (BIGINT NOT NULL): User ID of the group creator
- **creator_name** (VARCHAR(255) NOT NULL): Name of the group creator
- **max_members** (INTEGER NOT NULL): Maximum number of members allowed
- **meeting_type** (VARCHAR(50) NOT NULL): Type of meeting (ONLINE or OFFLINE)
- **meeting_schedule** (VARCHAR(255) NOT NULL): Schedule details (e.g., "Monday 2PM-4PM")
- **location** (VARCHAR(255)): Physical location for offline meetings
- **status** (VARCHAR(50) NOT NULL): Status of the group (PENDING, APPROVED, REJECTED)
- **created_at** (TIMESTAMP): Creation timestamp
- **updated_at** (TIMESTAMP): Last update timestamp

**Indexes:**
- idx_groups_status: For filtering by status
- idx_groups_creator_id: For finding groups by creator
- idx_groups_subject: For searching by subject

#### join_requests
- **id** (BIGSERIAL PRIMARY KEY): Unique identifier for the request
- **group_id** (BIGINT NOT NULL, FK): Reference to the group
- **user_id** (BIGINT NOT NULL): ID of the student requesting to join
- **user_name** (VARCHAR(255) NOT NULL): Name of the student
- **status** (VARCHAR(50) NOT NULL): Status of the request (PENDING, ACCEPTED, REJECTED)
- **message** (TEXT): Optional message from the student
- **created_at** (TIMESTAMP): Creation timestamp
- **updated_at** (TIMESTAMP): Last update timestamp

**Indexes:**
- idx_join_requests_group_id: For finding requests for a group
- idx_join_requests_user_id: For finding requests by user
- idx_join_requests_status: For filtering by status
- idx_join_requests_unique: Unique constraint on (group_id, user_id) for PENDING requests

#### group_members
- **id** (BIGSERIAL PRIMARY KEY): Unique identifier for the membership
- **group_id** (BIGINT NOT NULL, FK): Reference to the group
- **user_id** (BIGINT NOT NULL): ID of the member
- **user_name** (VARCHAR(255) NOT NULL): Name of the member
- **joined_at** (TIMESTAMP): Timestamp when the member joined

**Indexes & Constraints:**
- uk_group_members_unique: Unique constraint on (group_id, user_id)
- idx_group_members_group_id: For finding members of a group
- idx_group_members_user_id: For finding groups a user is a member of

## API Endpoints

### Public Endpoints (No Authentication Required)

#### 1. Get All Approved Groups
```
GET /api/groups?page=0&size=10
```
Returns paginated list of all approved groups.

**Response:**
```json
{
  "success": true,
  "message": "Groups fetched successfully",
  "data": {
    "content": [...],
    "totalElements": 42,
    "totalPages": 5,
    "currentPage": 0
  }
}
```

#### 2. Get Group by ID
```
GET /api/groups/{id}
```
Retrieves a specific group by its ID.

#### 3. Search Groups
```
GET /api/groups/search?q=python&page=0&size=10
```
Searches approved groups by name, subject, or location.

---

### Creator Endpoints (Authentication Required - CREATOR Role)

#### 4. Create Group
```
POST /api/groups
Headers: Authorization: Bearer {token}
Content-Type: application/json

{
  "name": "Advanced Python Study Group",
  "subject": "Python",
  "description": "For advanced Python learners",
  "maxMembers": 20,
  "meetingType": "ONLINE",
  "meetingSchedule": "Monday 2PM-4PM",
  "location": null
}
```

#### 5. Update Group
```
PUT /api/groups/{id}
Headers: Authorization: Bearer {token}
```
Only the creator can update their own group.

#### 6. Delete Group
```
DELETE /api/groups/{id}
Headers: Authorization: Bearer {token}
```
Only the creator can delete their own group.

---

### Admin Endpoints (Authentication Required - ADMIN Role)

#### 7. Approve Group
```
PUT /api/groups/{id}/approve
Headers: Authorization: Bearer {token}
```
Admin approval of a pending group.

#### 8. Reject Group
```
PUT /api/groups/{id}/reject
Headers: Authorization: Bearer {token}
```
Admin rejection of a pending group.

---

### Join Request Endpoints

#### 9. Create Join Request
```
POST /api/groups/{groupId}/join-requests
Headers: Authorization: Bearer {token}
Content-Type: application/json

{
  "message": "I'm interested in this study group"
}
```
Student requests to join a group.

#### 10. Get Join Requests (for Group Creator)
```
GET /api/groups/{groupId}/join-requests
Headers: Authorization: Bearer {token}
```
List all pending join requests for a group (creator only).

#### 11. Accept Join Request
```
PUT /api/groups/{groupId}/join-requests/{requestId}/accept
Headers: Authorization: Bearer {token}
```
Creator accepts a join request and adds user to group members.

#### 12. Reject Join Request
```
PUT /api/groups/{groupId}/join-requests/{requestId}/reject
Headers: Authorization: Bearer {token}
```
Creator rejects a join request.

---

### Member Endpoints

#### 13. Get Group Members
```
GET /api/groups/{id}/members
```
Get list of all members in a group (publicly accessible).

---

## WebSocket Events

The service broadcasts real-time updates via WebSocket at `/ws/groups`:

### Events Published
- **GROUP_CREATED**: When a new group is created
- **GROUP_APPROVED**: When a group is approved by admin
- **GROUP_REJECTED**: When a group is rejected
- **MEMBER_JOINED**: When a new member joins a group

### Example WebSocket Message
```json
{
  "eventType": "MEMBER_JOINED",
  "groupId": 1,
  "userId": 123,
  "userName": "John Doe",
  "groupName": "Advanced Python",
  "timestamp": "2024-05-02T10:30:00"
}
```

## Kafka Topics

- **group.created**: Published when a new group is created
- **group.approved**: Published when a group is approved
- **group.rejected**: Published when a group is rejected
- **group.member-joined**: Published when a member joins a group

## Status Codes

- `200 OK`: Successful GET/PUT request
- `201 Created`: Successful POST request
- `400 Bad Request`: Validation error
- `401 Unauthorized`: Missing or invalid authentication
- `403 Forbidden`: Insufficient permissions (not admin/creator)
- `404 Not Found`: Resource not found
- `500 Internal Server Error`: Server error

## Role-Based Access Control

| Endpoint | Anonymous | Student | Creator | Admin |
|----------|-----------|---------|---------|-------|
| GET /api/groups | ✓ | ✓ | ✓ | ✓ |
| GET /api/groups/{id} | ✓ | ✓ | ✓ | ✓ |
| GET /api/groups/search | ✓ | ✓ | ✓ | ✓ |
| POST /api/groups | ✗ | ✗ | ✓ | ✓ |
| PUT /api/groups/{id} | ✗ | ✗ | ✓ (own) | ✗ |
| DELETE /api/groups/{id} | ✗ | ✗ | ✓ (own) | ✗ |
| PUT /api/groups/{id}/approve | ✗ | ✗ | ✗ | ✓ |
| PUT /api/groups/{id}/reject | ✗ | ✗ | ✗ | ✓ |
| POST /api/groups/{id}/join-requests | ✗ | ✓ | ✓ | ✓ |
| GET /api/groups/{id}/join-requests | ✗ | ✗ | ✓ (own) | ✓ |
| PUT /api/groups/{id}/join-requests/{reqId}/accept | ✗ | ✗ | ✓ (own) | ✗ |
| PUT /api/groups/{id}/join-requests/{reqId}/reject | ✗ | ✗ | ✓ (own) | ✗ |
| GET /api/groups/{id}/members | ✓ | ✓ | ✓ | ✓ |

## Configuration

### application.yml Settings

```yaml
server.port: 8083
spring.datasource.url: jdbc:postgresql://localhost:5432/study_group_db
spring.jpa.hibernate.ddl-auto: update
spring.kafka.bootstrap-servers: localhost:9092
jwt.secret: <your-secret-key>
jwt.expiration: 86400000 (24 hours)
```

## Setup Instructions

1. **Start PostgreSQL Database**
   ```bash
   docker run -d -p 5432:5432 \
     -e POSTGRES_USER=postgres \
     -e POSTGRES_PASSWORD=postgres \
     -e POSTGRES_DB=study_group_db \
     postgres:latest
   ```

2. **Start Kafka** (if using Docker Compose)
   ```bash
   docker-compose up -d kafka zookeeper
   ```

3. **Build the Service**
   ```bash
   mvn clean package
   ```

4. **Run the Service**
   ```bash
   java -jar target/group-service-1.0.0.jar
   ```

5. **Verify Service**
   ```bash
   curl http://localhost:8083/api/groups
   ```

## Error Handling

All errors are returned in a standard format:

```json
{
  "success": false,
  "message": "Error description",
  "error": "ERROR_CODE",
  "data": null
}
```

### Common Error Codes
- `RESOURCE_NOT_FOUND`: Requested resource doesn't exist
- `UNAUTHORIZED`: Authentication required or invalid role
- `VALIDATION_ERROR`: Request validation failed
- `INVALID_REQUEST`: Invalid request parameters
- `INTERNAL_SERVER_ERROR`: Unexpected server error
