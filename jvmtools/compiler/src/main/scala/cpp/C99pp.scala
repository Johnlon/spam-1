//package cpp
//
//import scala.util.matching.Regex
//import scala.util.parsing.combinator.JavaTokenParsers
//
//// https://slebok.github.io/zoo/c/c99/iso-9899-tc3/extracted/index.html
//class C99pp extends JavaTokenParsers {
//
//  // tokens from here .. https://gist.github.com/codebrainz/2933703
//  def D = "[0-9]"
//
//  def L = "[a-zA-Z_]"
//
//  def H = "[a-fA-F0-9]"
//
//  def E = "([Ee][+-]?[0-9]+)"
//
//  def P = "([Pp][+-]?[0-9]+)"
//
//  def FS = "(f|F|l|L)"
//
//  def IS = "((u|U)|(u|U)?(l|L|ll|LL)|(l|L|ll|LL)(u|U))"
//
//  // tokens from here .. https://gist.github.com/codebrainz/2933703
//  def identifier: Parser[String] = "[a-zA-Z_][a-zA-Z0-9_]*".r ^^ (a => a)
//
//  // tokens from here .. https://gist.github.com/codebrainz/2933703
//  def constant: Parser[_] =
//    new Regex(s"0[xX]${H}+${IS}?") |
//      new Regex(s"0[0-7]*${IS}?") |
//      new Regex(s"[1-9]${D}*${IS}?") |
//      new Regex("L?'(\\.|[^\\'\n])+") |  // widechar= 16 bit chars   L"ABCD"  array of 16 bit words
//      new Regex(s"${D}+${E}${FS}?") |
//      new Regex(s"${D}*\\.${D}+${E}?${FS}?") |
//      new Regex(s"${D}+\\.${D}*${E}?${FS}?") |
//      new Regex(s"0[xX]${H}+${P}${FS}?") |
//      new Regex(s"0[xX]${H}*\\.${H}+${P}?${FS}?") |
//      new Regex(s"0[xX]${H}+\\.${H}*${P}?${FS}?")
//
//  //def string_literal = """L?\"(\\.|[^\\"\n])*\"""".r // NOT SURE WHAT TO DO WITH "L" STRINGS
//  def string_literal = """\"(\\.|[^\\"\n])*\"""".r
//
//  def translation_unit: Parser[_] = external_declaration | translation_unit ~ external_declaration
//
//  def external_declaration: Parser[_] = function_definition | declaration
//
//  def function_definition: Parser[_] = declaration_specifiers ~ declarator ~ opt(declaration_list) ~ compound_statement
//
//  def declaration_specifiers: Parser[_] = storage_class_specifier ~ opt(declaration_specifiers) |
//    type_specifier ~ opt(declaration_specifiers) |
//    type_qualifier ~ opt(declaration_specifiers) |
//    function_specifier ~ opt(declaration_specifiers)
//
//  def storage_class_specifier: Parser[_] = "typedef" | "extern" | "static" | "auto" | "register"
//
//  def type_specifier: Parser[_] = "void" |
//    "char" |
//    "short" |
//    "int" |
//    "long" |
//    "float" |
//    "double" |
//    "signed" |
//    "unsigned" |
//    "_Bool" |
//    "_Complex" |
//    struct_or_union_specifier |
//    enum_specifier |
//    typedef_name
//
//
//  def struct_or_union_specifier: Parser[_] = struct_or_union ~ opt(identifier) ~ "{" ~ struct_declaration_list ~ "}" |
//    struct_or_union ~ identifier
//
//  def struct_or_union: Parser[_] = "struct" | "union"
//
//  def struct_declaration_list: Parser[_] = struct_declaration | struct_declaration_list ~ struct_declaration
//
//  def struct_declaration: Parser[_] = specifier_qualifier_list ~ struct_declarator_list ~ ";"
//
//  def specifier_qualifier_list: Parser[_] = type_specifier ~ opt(specifier_qualifier_list) | type_qualifier ~ opt(specifier_qualifier_list)
//
//  def type_qualifier: Parser[_] = "const" | "restrict" | "volatile"
//
//  def struct_declarator_list: Parser[_] = struct_declarator | struct_declarator_list ~ "," ~ struct_declarator
//
//  def struct_declarator: Parser[_] = declarator | opt(declarator) ~ ":" ~ constant_expression
//
//  def declarator: Parser[_] = opt(pointer) ~ direct_declarator
//
//  def pointer: Parser[_] = "*" ~ opt(type_qualifier_list) | "*" ~ opt(type_qualifier_list) ~ pointer
//
//  def type_qualifier_list: Parser[_] = type_qualifier | type_qualifier_list ~ type_qualifier
//
//  def direct_declarator: Parser[_] = identifier |
//    "(" ~ declarator ~ ")" |
//    direct_declarator ~ "[" ~ opt(type_qualifier_list) ~ opt(assignment_expression) ~ "]" |
//    direct_declarator ~ "[" ~ "static" ~ opt(type_qualifier_list) ~ assignment_expression ~ "]" |
//    direct_declarator ~ "[" ~ type_qualifier_list ~ "static" ~ assignment_expression ~ "]" |
//    direct_declarator ~ "[" ~ opt(type_qualifier_list) ~ "*" ~ "]" |
//    direct_declarator ~ "(" ~ parameter_type_list ~ ")" |
//    direct_declarator ~ "(" ~ opt(identifier_list) ~ ")"
//
//  def assignment_expression: Parser[_] = conditional_expression | unary_expression ~ assignment_operator ~ assignment_expression
//
//  def conditional_expression: Parser[_] = logical_OR_expression | logical_OR_expression ~ "?" ~ expression ~ ":" ~ conditional_expression
//
//  def logical_OR_expression: Parser[_] = logical_AND_expression | logical_OR_expression ~ "||" ~ logical_AND_expression
//
//  def logical_AND_expression: Parser[_] = inclusive_OR_expression | logical_AND_expression ~ "&&" ~ inclusive_OR_expression
//
//  def inclusive_OR_expression: Parser[_] = exclusive_OR_expression | inclusive_OR_expression ~ "|" ~ exclusive_OR_expression
//
//  def exclusive_OR_expression: Parser[_] = AND_expression | exclusive_OR_expression ~ "^" ~ AND_expression
//
//  def AND_expression: Parser[_] = equality_expression | AND_expression ~ "&" ~ equality_expression
//
//  def equality_expression: Parser[_] = relational_expression | equality_expression ~ "==" ~ relational_expression | equality_expression ~ "!=" ~ relational_expression
//
//  def relational_expression: Parser[_] = shift_expression |
//    relational_expression ~ "<" ~ shift_expression |
//    relational_expression ~ ">" ~ shift_expression |
//    relational_expression ~ "<=" ~ shift_expression |
//    relational_expression ~ ">=" ~ shift_expression
//
//  def shift_expression: Parser[_] = additive_expression | shift_expression ~ "<<" ~ additive_expression | shift_expression ~ ">>" ~ additive_expression
//
//  def additive_expression: Parser[_] = multiplicative_expression | additive_expression ~ "+" ~ multiplicative_expression | additive_expression ~ "_" ~ multiplicative_expression
//
//  def multiplicative_expression: Parser[_] = cast_expression | multiplicative_expression ~ "*" ~ cast_expression | multiplicative_expression ~ "/" ~ cast_expression | multiplicative_expression ~ "%" ~ cast_expression
//
//  def cast_expression: Parser[_] = unary_expression | "(" ~ type_name ~ ")" ~ cast_expression
//
//  def unary_expression: Parser[_] = postfix_expression |
//    "++" ~ unary_expression |
//    "__" ~ unary_expression |
//    unary_operator ~ cast_expression |
//    "sizeof" ~ unary_expression |
//    "sizeof" ~ "(" ~ type_name ~ ")"
//
//  def postfix_expression: Parser[_] =
//    primary_expression |
//      postfix_expression ~ "[" ~ expression ~ "]" |
//      postfix_expression ~ "(" ~ opt(argument_expression_list) ~ ")" |
//      postfix_expression ~ "." ~ identifier |
//      postfix_expression ~ "_>" ~ identifier |
//      postfix_expression ~ "++" |
//      postfix_expression ~ "__" |
//      "(" ~ type_name ~ ")" ~ "{" ~ initializer_list ~ "}" |
//      "(" ~ type_name ~ ")" ~ "{" ~ initializer_list ~ "," ~ "}"
//
//  def primary_expression: Parser[_] =
//    identifier |
//      constant |
//      string_literal |
//      "(" ~ expression ~ ")"
//
//  def expression: Parser[_] =
//    assignment_expression |
//      expression ~ "," ~ assignment_expression
//
//  def argument_expression_list: Parser[_] =
//    assignment_expression |
//      argument_expression_list ~ "," ~ assignment_expression
//
//  def type_name: Parser[_] =
//    specifier_qualifier_list ~ opt(abstract_declarator)
//
//  def abstract_declarator: Parser[_] =
//    pointer |
//      opt(pointer) ~ direct_abstract_declarator
//
//  def direct_abstract_declarator: Parser[_] =
//    "(" ~ abstract_declarator ~ ")" |
//      opt(direct_abstract_declarator) ~ "[" ~ opt(type_qualifier_list) ~ opt(assignment_expression) ~ "]" |
//      opt(direct_abstract_declarator) ~ "[" ~ "static" ~ opt(type_qualifier_list) ~ assignment_expression ~ "]" |
//      opt(direct_abstract_declarator) ~ "[" ~ type_qualifier_list ~ "static" ~ assignment_expression ~ "]" |
//      opt(direct_abstract_declarator) ~ "[" ~ "*" ~ "]" |
//      opt(direct_abstract_declarator) ~ "(" ~ opt(parameter_type_list) ~ ")"
//
//  def parameter_type_list: Parser[_] = parameter_list | parameter_list ~ "," ~ "..."
//
//
//  def parameter_list: Parser[_] = parameter_declaration | parameter_list ~ "," ~ parameter_declaration
//
//  def parameter_declaration: Parser[_] = declaration_specifiers ~ declarator | declaration_specifiers ~ opt(abstract_declarator)
//
//  def initializer_list: Parser[_] = opt(designation) ~ initializer | initializer_list ~ "," ~ opt(designation) ~ initializer
//
//  def designation: Parser[_] = designator_list ~ "="
//
//
//  def designator_list: Parser[_] = designator | designator_list ~ designator
//
//  def designator: Parser[_] = "[" ~ constant_expression ~ "]" | "." ~ identifier
//
//  def constant_expression: Parser[_] = conditional_expression
//
//  def initializer: Parser[_] = assignment_expression | "{" ~ initializer_list ~ "}" | "{" ~ initializer_list ~ "," ~ "}"
//
//  def unary_operator: Parser[_] = "&" | "*" | "+" | "-" | "|" | "!"
//
//  def assignment_operator: Parser[_] = "=" | "*=" | "/=" | "%=" | "+=" | "-=" | "<<=" | ">>=" | "&=" | "^=" | "|="
//
//  def identifier_list: Parser[_] = identifier | identifier_list ~ "," ~ identifier
//
//  def enum_specifier: Parser[_] =
//    "enum" ~ opt(identifier) ~ "{" ~ enumerator_list ~ "}" |
//      "enum" ~ opt(identifier) ~ "{" ~ enumerator_list ~ "," ~ "}" |
//      "enum" ~ identifier
//
//  def enumerator_list: Parser[_] = enumerator | enumerator_list ~ "," ~ enumerator
//
//  def enumeration_constant: Parser[String] = identifier
//
//  def enumerator: Parser[_] = enumeration_constant | enumeration_constant ~ "=" ~ constant_expression
//
//  def typedef_name: Parser[_] = identifier
//
//  def function_specifier: Parser[_] = "inline"
//
//  def declaration_list: Parser[_] = declaration | declaration_list ~ declaration
//
//  def declaration: Parser[_] = declaration_specifiers ~ opt(init_declarator_list) ~ ";"
//
//  def init_declarator_list: Parser[_] = init_declarator | init_declarator_list ~ "," ~ init_declarator
//
//  def init_declarator: Parser[_] = declarator | declarator ~ "=" ~ initializer
//
//  def compound_statement: Parser[_] = "{" ~ opt(block_item_list) ~ "}"
//
//  def block_item_list: Parser[_] = block_item | block_item_list ~ block_item
//
//  def block_item: Parser[_] = declaration | statement
//
//  def statement: Parser[_] =
//    labeled_statement |
//      compound_statement |
//      expression_statement |
//      selection_statement |
//      iteration_statement |
//      jump_statement
//
//  def labeled_statement: Parser[_] =
//    identifier ~ ":" ~ statement |
//      "case" ~ constant_expression ~ ":" ~ statement |
//      "default" ~ ":" ~ statement
//
//  def expression_statement: Parser[_] = opt(expression) ~ ";"
//
//  def selection_statement: Parser[_] =
//    "if" ~ "(" ~ expression ~ ")" ~ statement |
//      "if" ~ "(" ~ expression ~ ")" ~ statement ~ "else" ~ statement |
//      "switch" ~ "(" ~ expression ~ ")" ~ statement
//
//  def iteration_statement: Parser[_] =
//    "while" ~ "(" ~ expression ~ ")" ~ statement |
//      "do" ~ statement ~ "while" ~ "(" ~ expression ~ ")" ~ ";" |
//      "for" ~ "(" ~ opt(expression) ~ ";" ~ opt(expression) ~ ";" ~ opt(expression) ~ ")" ~ statement |
//      "for" ~ "(" ~ declaration ~ opt(expression) ~ ";" ~ opt(expression) ~ ")" ~ statement
//
//  def jump_statement: Parser[_] =
//    "goto" ~ identifier ~ ";" |
//      "continue" ~ ";" |
//      "break" ~ ";" |
//      "return" ~ opt(expression) ~ ";"
//}