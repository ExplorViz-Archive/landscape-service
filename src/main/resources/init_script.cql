CREATE KEYSPACE IF NOT EXISTS explorviz WITH replication = {'class':'SimpleStrategy', 'replication_factor':1};


CREATE TABLE IF NOT EXISTS explorviz.span_structure
(
    landscape_token      text,
    timestamp            bigint,
    hash_code            text,
    host_name            text,
    host_ip_address      text,
    application_name     text,
    application_language text,
    instance_id          text,
    method_fqn           text,
    PRIMARY KEY ((landscape_token), hash_code)
);

CREATE INDEX IF NOT EXISTS timestamp_index ON explorviz.span_structure (timestamp);
