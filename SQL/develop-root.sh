cat develop.sql > root.sql
mysql -p --verbose --show-warnings < root.sql
rm root.sql
