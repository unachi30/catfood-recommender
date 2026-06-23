-- One-time cleanup: remove all whitespace from brand and name in existing rows.
-- Run in DBeaver when old imports used spaced variants like "Cherie 法麗".

UPDATE cat_cans
SET brand = REGEXP_REPLACE(brand, '[[:space:]]+', ''),
    name = REGEXP_REPLACE(name, '[[:space:]]+', '')
WHERE brand REGEXP '[[:space:]]' OR name REGEXP '[[:space:]]';
