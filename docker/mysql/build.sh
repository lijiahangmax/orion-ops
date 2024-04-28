mv ../../sql/init-schema.sql ./
mv ../../sql/init-data.sql ./
docker build -t orion-ops-mysql:1.2.3 .
rm -f ./init-schema.sql
rm -f ./init-data.sql