-- --- Lookups ---
CREATE TABLE priorities (
  id   SMALLINT PRIMARY KEY,              -- 1..5
  name TEXT NOT NULL
);
INSERT INTO priorities (id,name) VALUES
(1,'Lowest'),(2,'Low'),(3,'Medium'),(4,'High'),(5,'Highest')
ON CONFLICT DO NOTHING;

CREATE TABLE statuses (
  id      SMALLINT PRIMARY KEY,           -- 1..5
  name    TEXT NOT NULL,
  is_done BOOLEAN NOT NULL DEFAULT FALSE
);
INSERT INTO statuses (id,name,is_done) VALUES
(1,'Backlog',FALSE),(2,'Todo',FALSE),(3,'In Progress',FALSE),(4,'Blocked',FALSE),(5,'Done',TRUE)
ON CONFLICT DO NOTHING;

-- --- Tasks ---
CREATE TABLE tasks (
  id               UUID PRIMARY KEY,
  account_id       UUID,
  parent_task_id   UUID REFERENCES tasks(id) ON DELETE CASCADE,
  title            TEXT NOT NULL,
  description_md   TEXT,
  status_id        SMALLINT NOT NULL REFERENCES statuses(id),
  priority_id      SMALLINT NOT NULL DEFAULT 3 REFERENCES priorities(id),
  order_index      BIGINT NOT NULL DEFAULT 0,
  estimate_minutes INTEGER CHECK (estimate_minutes >= 0),
  spent_minutes    INTEGER NOT NULL DEFAULT 0 CHECK (spent_minutes >= 0),
  points           NUMERIC(5,2),
  start_at         TIMESTAMPTZ,
  due_at           TIMESTAMPTZ,
  completed_at     TIMESTAMPTZ,
  recurrence_rrule TEXT,
  timezone         TEXT,
  is_archived      BOOLEAN NOT NULL DEFAULT FALSE,
  deleted_at       TIMESTAMPTZ,
  version          INTEGER NOT NULL DEFAULT 1,
  extras           JSONB NOT NULL DEFAULT '{}'::jsonb,
  created_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at       TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_tasks_parent  ON tasks(parent_task_id);
CREATE INDEX idx_tasks_status  ON tasks(status_id);
CREATE INDEX idx_tasks_due     ON tasks(due_at) WHERE deleted_at IS NULL AND is_archived = FALSE;
CREATE INDEX idx_tasks_search  ON tasks USING GIN (to_tsvector('simple', coalesce(title,'') || ' ' || coalesce(description_md,'')));

-- --- Labels ---
CREATE TABLE labels (
  id     UUID PRIMARY KEY,
  name   TEXT NOT NULL,
  color  TEXT,
  UNIQUE (name)
);
CREATE TABLE task_labels (
  task_id  UUID NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
  label_id UUID NOT NULL REFERENCES labels(id) ON DELETE CASCADE,
  PRIMARY KEY (task_id, label_id)
);

-- --- Dependencies (blockers) ---
CREATE TABLE task_dependencies (
  task_id        UUID NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
  depends_on_id  UUID NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
  dep_type       TEXT NOT NULL CHECK (dep_type IN ('FS','SS','FF','SF')),
  PRIMARY KEY (task_id, depends_on_id),
  CHECK (task_id <> depends_on_id)
);

-- --- Checklists ---
CREATE TABLE checklists (
  id          UUID PRIMARY KEY,
  task_id     UUID NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
  title       TEXT,
  order_index BIGINT NOT NULL DEFAULT 0
);
CREATE TABLE checklist_items (
  id            UUID PRIMARY KEY,
  checklist_id  UUID NOT NULL REFERENCES checklists(id) ON DELETE CASCADE,
  content       TEXT NOT NULL,
  is_done       BOOLEAN NOT NULL DEFAULT FALSE,
  order_index   BIGINT NOT NULL DEFAULT 0,
  done_at       TIMESTAMPTZ
);

-- --- Comments ---
CREATE TABLE comments (
  id          UUID PRIMARY KEY,
  task_id     UUID NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
  author_name TEXT,
  body_md     TEXT NOT NULL,
  created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at  TIMESTAMPTZ
);
CREATE INDEX idx_comments_task ON comments(task_id);

-- --- Attachments ---
CREATE TABLE attachments (
  id           UUID PRIMARY KEY,
  task_id      UUID NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
  filename     TEXT NOT NULL,
  mime_type    TEXT,
  size_bytes   BIGINT CHECK (size_bytes >= 0),
  storage_uri  TEXT NOT NULL,
  created_at   TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- --- Reminders ---
CREATE TABLE reminders (
  id         UUID PRIMARY KEY,
  task_id    UUID NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
  remind_at  TIMESTAMPTZ NOT NULL,
  channel    TEXT NOT NULL CHECK (channel IN ('local','push','email','sms','webhook')),
  payload    JSONB NOT NULL DEFAULT '{}'::jsonb
);
CREATE INDEX idx_reminders_due ON reminders(remind_at);