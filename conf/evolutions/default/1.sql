# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table task_mst (
  id                            bigint not null,
  task_name                     varchar(255),
  task_info                     varchar(255),
  rep_type                      varchar(255),
  repetition                    varchar(255),
  constraint pk_task_mst primary key (id)
);
create sequence task_mst_seq;

create table task_trn (
  id                            bigint not null,
  task_mst_id                   bigint,
  task_date                     timestamp,
  operation_flg                 varchar(255),
  operation_user_id             bigint,
  constraint uq_task_trn_operation_user_id unique (operation_user_id),
  constraint pk_task_trn primary key (id)
);
create sequence task_trn_seq;

create table team (
  id                            bigint not null,
  team_name                     varchar(255),
  create_user_id                bigint,
  constraint pk_team primary key (id)
);
create sequence team_seq;

create table user (
  id                            bigint not null,
  user_name                     varchar(255),
  password                      varchar(255),
  constraint pk_user primary key (id)
);
create sequence user_seq;

create table user_team (
  user_id                       bigint not null,
  team_id                       bigint not null,
  constraint pk_user_team primary key (user_id,team_id)
);

alter table task_trn add constraint fk_task_trn_task_mst_id foreign key (task_mst_id) references task_mst (id) on delete restrict on update restrict;
create index ix_task_trn_task_mst_id on task_trn (task_mst_id);

alter table task_trn add constraint fk_task_trn_operation_user_id foreign key (operation_user_id) references user (id) on delete restrict on update restrict;

alter table team add constraint fk_team_create_user_id foreign key (create_user_id) references user (id) on delete restrict on update restrict;
create index ix_team_create_user_id on team (create_user_id);

alter table user_team add constraint fk_user_team_user foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_user_team_user on user_team (user_id);

alter table user_team add constraint fk_user_team_team foreign key (team_id) references team (id) on delete restrict on update restrict;
create index ix_user_team_team on user_team (team_id);


# --- !Downs

alter table task_trn drop constraint if exists fk_task_trn_task_mst_id;
drop index if exists ix_task_trn_task_mst_id;

alter table task_trn drop constraint if exists fk_task_trn_operation_user_id;

alter table team drop constraint if exists fk_team_create_user_id;
drop index if exists ix_team_create_user_id;

alter table user_team drop constraint if exists fk_user_team_user;
drop index if exists ix_user_team_user;

alter table user_team drop constraint if exists fk_user_team_team;
drop index if exists ix_user_team_team;

drop table if exists task_mst;
drop sequence if exists task_mst_seq;

drop table if exists task_trn;
drop sequence if exists task_trn_seq;

drop table if exists team;
drop sequence if exists team_seq;

drop table if exists user;
drop sequence if exists user_seq;

drop table if exists user_team;

