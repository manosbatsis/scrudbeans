---
title: RSQL Support
---

ScrudBeans supports search by either URL params or RSQL and a `filter` param. RSQL is a query
language for parametrized filtering of entries in RESTful APIs.

It’s based on <a href="http://tools.ietf.org/html/draft-nottingham-atompub-fiql-00">FIQL</a> (Feed Item Query Language), a URI-friendly syntax for expressing filters across the entries in an Atom Feed.
FIQL is great for use in a URI; there are no unsafe characters, so URL encoding is not required.
On the other side, FIQL’s syntax is not very intuitive and URL encoding isn’t always that big deal, so RSQL also provides a friendlier syntax for logical operators and some of the comparison operators.

For example, you can query your resource like this: `/movies?query=name=="Kill Bill";year=gt=2003` or `/movies?query=director.lastName==Nolan and year>=2000`.
See below for examples.

ScrudBeans' RSQL support is based on <a href="https://github.com/jirutka/rsql-parser">rsql-parser</a>, a complete and thoroughly tested parser for RSQL written in <a href="http://javacc.java.net">JavaCC</a> and Java.
Since RSQL is a superset of the FIQL, it can be used for parsing FIQL as well.

## Grammar

_The following grammar specification is written in EBNF notation (<a href="http://www.cl.cam.ac.uk/~mgk25/iso-14977.pdf">ISO 14977</a>)_.

RSQL expression is composed of one or more comparisons, related to each other with logical operators:

- Logical AND: `;` or `and`
- Logical OR: `,` or `or`

By default, the AND operator takes precedence (i.e. it’s evaluated before any OR operators are).
However, a parenthesized expression can be used to change the precedence, yielding whatever the contained expression yields.

```
input          = or, EOF;
or             = and, { "," , and };
and            = constraint, { ";" , constraint };
constraint     = ( group | comparison );
group          = "(", or, ")";
```

Comparison is composed of a selector, an operator and an argument.

```
comparison     = selector, comparison-op, arguments;
```

Selector identifies a field (or attribute, element, …) of the resource representation to filter by.
It can be any non empty Unicode string that doesn’t contain reserved characters (see below) or a white space.
The specific syntax of the selector is not enforced by this parser.

```selector       = unreserved-str;```

Comparison operators are in FIQL notation and some of them has an alternative syntax as well:

- Equal to : `==`
- Not equal to : `!=`
- Less than : `=lt=` or `&lt;`
- Less than or equal to : `=le=` or `⇐`
- Greater than operator : `=gt=` or `&gt;`
- Greater than or equal to : `=ge=` or `&gt;=`
- In : `=in=`
- Not in : `=out=`

You can also add your own operators.

```
comparison-op  = comp-fiql | comp-alt;
comp-fiql      = ( ( "=", { ALPHA } ) | "!" ), "=";
comp-alt       = ( "&gt;" | "&lt;" ), [ "=" ];
```

Argument can be a single value, or multiple values in parenthesis separated by comma.
Value that doesn’t contain any reserved character or a white space can be unquoted, other arguments must be enclosed in single or double quotes.

```
arguments      = ( "(", value, { "," , value }, ")" ) | value;
value          = unreserved-str | double-quoted | single-quoted;

unreserved-str = unreserved, { unreserved }
single-quoted  = "'", { ( escaped | all-chars - ( "'" | "\" ) ) }, "'";
double-quoted  = '"', { ( escaped | all-chars - ( '"' | "\" ) ) }, '"';

reserved       = '"' | "'" | "(" | ")" | ";" | "," | "=" | "!" | "~" | "&lt;" | "&gt;";
unreserved     = all-chars - reserved - " ";
escaped        = "\", all-chars;
all-chars      = ? all unicode characters ?;
```

If you need to use both single and double quotes inside a quoted argument, then you must escape one of them using `\` (backslash).
If you want to use `\` literally, then double it as `\\`.
Backslash has a special meaning only inside a quoted argument, not in unquoted argument.

## Examples

Examples of RSQL expressions in both FIQL-like and alternative notation:

```
?filter=name=="Kill Bill";year=gt=2003
?filter=name=="Kill Bill" and year&gt;2003
?filter=genres=in=(sci-fi,action);(director=='Christopher Nolan',actor==*Bale);year=ge=2000
?filter=genres=in=(sci-fi,action) and (director=='Christopher Nolan' or actor==*Bale) and year&gt;=2000
?filter=director.lastName==Nolan;year=ge=2000;year=lt=2010
?filter=director.lastName==Nolan and year&gt;=2000 and year&lt;2010
?filter=genres=in=(sci-fi,action);genres=out=(romance,animated,horror),director==Que*Tarantino
?filter=genres=in=(sci-fi,action) and genres=out=(romance,animated,horror) or director==Que*Tarantino
```
