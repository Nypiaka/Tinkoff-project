create table if not exists links
(
	id bigserial primary key,
  link varchar unique
);

create table if not exists content_by_link
(
  link_id bigint references links (id),
  content varchar,
  updated_at timestamp,
  unique(link_id),
  primary key (link_id)
);

create table if not exists chats_to_links
(
  chat_id bigint,
  link_id bigint references links (id),
  primary key (chat_id, link_id)
);
