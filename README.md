COMPOSITEPDF
============

Composite PDF is a Java wrapper around iText functionality, allowing an
XML-formatted input file and a PDF template to create a "composite" PDF
file. This is useful for things like programmatically filling out PDF
forms.

PREREQUISITES
-------------

 * JRE 1.5+
 * Maven2

BUILDING
--------

`mvn package` will generate a jar file in `target/`.

USAGE
-----

```
options:
	[-i|--input]     Input file name
	[-t|--template]  PDF template
	[-o|--output]    Output file name
```

