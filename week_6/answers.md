# Answers

## 6.1
- The minimum sensible number of voting nodes to a replica set is three.
- The oplog utilizes a capped collection.

## 6.2
- w="majority", j=1

## 6.3

- MongoDB can not enforce unique indexes on a sharded collection other than the shard key itself, or indexes prefixed by the shard key.
- Any update that does not contain the shard key will be sent to all shards.
- There must be a index on the collection that starts with the shard key.

## 6.4

- s1