prompt PL/SQL Developer import file
prompt Created on 2014Äê6ÔÂ27ÈÕ by pgy
set feedback off
set define off
prompt Dropping T_POS_PKTDEF...
drop table T_POS_PKTDEF cascade constraints;
prompt Creating T_POS_PKTDEF...
create table T_POS_PKTDEF
(
  pkt_id   NUMBER(10),
  msg_type VARCHAR2(4),
  bit      NUMBER(4),
  type     VARCHAR2(10),
  format   VARCHAR2(10),
  spec     VARCHAR2(10),
  length   NUMBER(4),
  stat     CHAR(3)
)
;

prompt Disabling triggers for T_POS_PKTDEF...
alter table T_POS_PKTDEF disable all triggers;
prompt Loading T_POS_PKTDEF...
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (1, '0200', 0, 'BCD', 'FIXED', null, 4, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (2, '0200', 1, 'BIT', 'FIXED', null, 8, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (3, '0200', 2, 'BCD', 'LLVAR', null, 19, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (4, '0200', 3, 'BCD', 'FIXED', null, 6, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (5, '0200', 4, 'BCD', 'FIXED', null, 12, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (6, '0200', 5, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (7, '0200', 6, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (8, '0200', 7, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (9, '0200', 8, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (10, '0200', 9, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (11, '0200', 10, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (12, '0200', 11, 'BCD', 'FIXED', null, 6, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (13, '0200', 12, 'BCD', 'FIXED', null, 6, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (14, '0200', 13, 'BCD', 'FIXED', null, 4, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (15, '0200', 14, 'BCD', 'FIXED', null, 4, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (16, '0200', 15, 'BCD', 'FIXED', null, 4, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (17, '0200', 16, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (18, '0200', 17, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (19, '0200', 18, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (20, '0200', 19, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (21, '0200', 20, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (22, '0200', 21, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (23, '0200', 22, 'BCD', 'FIXED', null, 3, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (24, '0200', 23, 'BCD', 'FIXED', null, 3, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (25, '0200', 24, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (26, '0200', 25, 'BCD', 'FIXED', null, 2, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (27, '0200', 26, 'BCD', 'FIXED', null, 2, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (28, '0200', 27, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (29, '0200', 28, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (30, '0200', 29, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (31, '0200', 30, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (32, '0200', 31, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (33, '0200', 32, 'BCD', 'LLVAR', null, 11, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (34, '0200', 33, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (35, '0200', 34, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (36, '0200', 35, 'BCD', 'LLVAR', null, 37, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (37, '0200', 36, 'BCD', 'LLVAR', null, 104, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (38, '0200', 37, 'ASC', 'FIXED', null, 12, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (39, '0200', 38, 'ASC', 'FIXED', null, 6, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (40, '0200', 39, 'ASC', 'FIXED', null, 2, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (41, '0200', 40, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (42, '0200', 41, 'ASC', 'FIXED', null, 8, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (43, '0200', 42, 'ASC', 'FIXED', null, 15, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (44, '0200', 43, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (45, '0200', 44, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (46, '0200', 45, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (47, '0200', 46, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (48, '0200', 47, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (49, '0200', 48, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (50, '0200', 49, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (51, '0200', 50, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (52, '0200', 51, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (53, '0200', 52, 'BIN', 'FIXED', null, 8, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (54, '0200', 53, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (55, '0200', 54, 'ASC', 'FIXED', null, 20, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (56, '0200', 55, 'BCD', 'FIXED', null, 255, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (57, '0200', 56, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (58, '0200', 57, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (59, '0200', 58, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (60, '0200', 59, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (61, '0200', 60, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (62, '0200', 61, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (63, '0200', 62, 'ASC', 'LLLVAR', null, 512, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (64, '0200', 63, 'ASC', 'LLLVAR', null, 63, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (65, '0200', 64, 'BIN', 'FIXED', null, 8, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (61, '0220', 0, 'BCD', 'FIXED', null, 4, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (62, '0220', 1, 'BIT', 'FIXED', null, 16, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (63, '0220', 2, 'BCD', 'LLVAR', null, 30, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (64, '0220', 3, 'BCD', 'FIXED', null, 6, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (65, '0220', 4, 'BCD', 'FIXED', null, 12, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (66, '0220', 5, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (67, '0220', 6, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (68, '0220', 7, 'BCD', 'FIXED', null, 10, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (69, '0220', 8, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (70, '0220', 9, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (71, '0220', 10, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (72, '0220', 11, 'BCD', 'FIXED', null, 6, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (73, '0220', 12, 'BCD', 'FIXED', null, 6, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (74, '0220', 13, 'BCD', 'FIXED', null, 8, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (75, '0220', 14, 'BCD', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (76, '0220', 15, 'BCD', 'FIXED', null, 4, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (77, '0220', 16, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (78, '0220', 17, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (79, '0220', 18, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (80, '0220', 19, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (81, '0220', 20, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (82, '0220', 21, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (83, '0220', 22, 'BCD', 'FIXED', null, 4, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (84, '0220', 23, 'BCD', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (85, '0220', 24, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (86, '0220', 25, 'BCD', 'FIXED', null, 2, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (87, '0220', 26, 'BCD', 'FIXED', null, 2, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (88, '0220', 27, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (89, '0220', 28, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (90, '0220', 29, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (91, '0220', 30, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (92, '0220', 31, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (93, '0220', 32, 'BCD', 'LLVAR', null, 6, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (94, '0220', 33, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (95, '0220', 34, 'ASC', 'FIXED', null, null, 'S0A');
commit;
prompt 100 records committed...
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (96, '0220', 35, 'BCD', 'LLVAR', null, 37, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (97, '0220', 36, 'BCD', 'LLVAR', null, 104, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (98, '0220', 37, 'ASC', 'FIXED', null, 12, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (99, '0220', 38, 'ASC', 'FIXED', null, 6, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (100, '0220', 39, 'ASC', 'FIXED', null, 2, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (101, '0220', 40, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (102, '0220', 41, 'ASC', 'FIXED', null, 8, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (103, '0220', 42, 'ASC', 'FIXED', null, 15, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (104, '0220', 43, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (105, '0220', 44, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (106, '0220', 45, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (107, '0220', 46, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (108, '0220', 47, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (109, '0220', 48, 'ASC', 'LLLVAR', null, 40, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (110, '0220', 49, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (111, '0220', 50, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (112, '0220', 51, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (113, '0220', 52, 'BIN', 'FIXED', null, 16, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (114, '0220', 53, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (115, '0220', 54, 'ASC', 'FIXED', null, 20, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (116, '0220', 55, 'BCD', 'FIXED', null, 255, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (117, '0220', 56, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (118, '0220', 57, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (119, '0220', 58, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (120, '0220', 59, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (121, '0220', 60, 'ASC', 'LLLVAR', null, 999, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (122, '0220', 61, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (123, '0220', 62, 'ASC', 'LLLVAR', null, 512, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (124, '0220', 63, 'ASC', 'LLLVAR', null, 63, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (125, '0220', 64, 'BIN', 'FIXED', null, 8, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (126, '0220', 65, 'BCD', 'FIXED', null, 4, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (127, '0220', 66, 'BIT', 'FIXED', null, 8, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (128, '0220', 67, 'BCD', 'LLVAR', null, 19, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (129, '0220', 68, 'BCD', 'FIXED', null, 6, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (130, '0220', 69, 'BCD', 'FIXED', null, 12, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (131, '0220', 70, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (132, '0220', 71, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (133, '0220', 72, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (134, '0220', 73, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (135, '0220', 74, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (136, '0220', 75, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (137, '0220', 76, 'BCD', 'FIXED', null, 6, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (138, '0220', 77, 'BCD', 'FIXED', null, 6, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (139, '0220', 78, 'BCD', 'FIXED', null, 4, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (140, '0220', 79, 'BCD', 'FIXED', null, 4, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (141, '0220', 80, 'BCD', 'FIXED', null, 4, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (142, '0220', 81, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (143, '0220', 82, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (144, '0220', 83, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (145, '0220', 84, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (146, '0220', 85, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (147, '0220', 86, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (148, '0220', 87, 'BCD', 'FIXED', null, 3, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (149, '0220', 88, 'BCD', 'FIXED', null, 3, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (150, '0220', 89, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (151, '0220', 90, 'BCD', 'FIXED', null, 2, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (152, '0220', 91, 'BCD', 'FIXED', null, 2, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (153, '0220', 92, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (154, '0220', 93, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (155, '0220', 94, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (156, '0220', 95, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (157, '0220', 96, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (158, '0220', 97, 'BCD', 'LLVAR', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (159, '0220', 98, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (160, '0220', 99, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (161, '0220', 100, 'BCD', 'LLVAR', null, 6, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (162, '0220', 101, 'BCD', 'LLVAR', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (163, '0220', 102, 'ASC', 'LLVAR', null, 30, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (164, '0220', 103, 'ASC', 'FIXED', null, 6, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (165, '0220', 104, 'ASC', 'LLLVAR', null, 90, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (166, '0220', 105, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (167, '0220', 106, 'ASC', 'FIXED', null, 8, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (168, '0220', 107, 'ASC', 'FIXED', 'L_BLANK', 15, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (169, '0220', 108, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (170, '0220', 109, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (171, '0220', 110, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (172, '0220', 111, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (173, '0220', 112, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (174, '0220', 113, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (175, '0220', 114, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (176, '0220', 115, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (177, '0220', 116, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (178, '0220', 117, 'BIN', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (179, '0220', 118, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (180, '0220', 119, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (181, '0220', 120, 'BCD', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (182, '0220', 121, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (183, '0220', 122, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (184, '0220', 123, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (185, '0220', 124, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (186, '0220', 125, 'ASC', 'LLLVAR', null, 30, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (187, '0220', 126, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (188, '0220', 127, 'ASC', 'LLLVAR', null, 60, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (189, '0220', 128, 'BIN', 'FIXED', null, 16, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (261, '0210', 0, 'ASC', 'FIXED', null, 4, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (262, '0210', 1, 'BIT', 'FIXED', null, 8, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (263, '0210', 2, 'ASC', 'LLVAR', null, 19, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (264, '0210', 3, 'ASC', 'FIXED', null, 6, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (265, '0210', 4, 'ASC', 'FIXED', null, 12, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (266, '0210', 5, 'ASC', 'FIXED', null, null, 'S0A');
commit;
prompt 200 records committed...
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (267, '0210', 6, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (268, '0210', 7, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (269, '0210', 8, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (270, '0210', 9, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (271, '0210', 10, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (272, '0210', 11, 'ASC', 'FIXED', null, 6, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (273, '0210', 12, 'ASC', 'FIXED', null, 6, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (274, '0210', 13, 'ASC', 'FIXED', null, 4, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (275, '0210', 14, 'ASC', 'FIXED', null, 4, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (276, '0210', 15, 'ASC', 'FIXED', null, 4, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (277, '0210', 16, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (278, '0210', 17, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (279, '0210', 18, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (280, '0210', 19, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (281, '0210', 20, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (282, '0210', 21, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (283, '0210', 22, 'ASC', 'FIXED', null, 3, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (284, '0210', 23, 'ASC', 'FIXED', null, 3, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (285, '0210', 24, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (286, '0210', 25, 'ASC', 'FIXED', null, 2, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (287, '0210', 26, 'ASC', 'FIXED', null, 2, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (288, '0210', 27, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (289, '0210', 28, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (290, '0210', 29, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (291, '0210', 30, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (292, '0210', 31, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (293, '0210', 32, 'ASC', 'LLVAR', null, 11, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (294, '0210', 33, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (295, '0210', 34, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (296, '0210', 35, 'BCD', 'LLVAR', null, 37, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (297, '0210', 36, 'BCD', 'LLVAR', null, 104, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (298, '0210', 37, 'ASC', 'FIXED', null, 12, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (299, '0210', 38, 'ASC', 'FIXED', null, 6, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (300, '0210', 39, 'ASC', 'FIXED', null, 2, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (301, '0210', 40, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (302, '0210', 41, 'ASC', 'FIXED', null, 8, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (303, '0210', 42, 'ASC', 'FIXED', null, 15, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (304, '0210', 43, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (305, '0210', 44, 'ASC', 'LLVAR', null, 25, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (306, '0210', 45, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (307, '0210', 46, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (308, '0210', 47, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (309, '0210', 48, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (310, '0210', 49, 'ASC', 'FIXED', null, 3, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (311, '0210', 50, 'ASC', 'FIXED', null, 16, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (312, '0210', 51, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (313, '0210', 52, 'BIN', 'FIXED', null, 16, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (314, '0210', 53, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (315, '0210', 54, 'ASC', 'FIXED', null, 20, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (316, '0210', 55, 'ASC', 'LLLVAR', null, 255, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (317, '0210', 56, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (318, '0210', 57, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (319, '0210', 58, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (320, '0210', 59, 'ASC', 'FIXED', null, null, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (321, '0210', 60, 'ASC', 'LLLVAR', null, 13, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (322, '0210', 61, 'ASC', 'LLLVAR', null, 200, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (323, '0210', 62, 'ASC', 'LLLVAR', null, 512, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (324, '0210', 63, 'ASC', 'LLLVAR', null, 63, 'S0A');
insert into T_POS_PKTDEF (pkt_id, msg_type, bit, type, format, spec, length, stat)
values (325, '0210', 64, 'BIN', 'FIXED', null, 8, 'S0A');
commit;
prompt 259 records loaded
prompt Enabling triggers for T_POS_PKTDEF...
alter table T_POS_PKTDEF enable all triggers;
set feedback on
set define on
prompt Done.
