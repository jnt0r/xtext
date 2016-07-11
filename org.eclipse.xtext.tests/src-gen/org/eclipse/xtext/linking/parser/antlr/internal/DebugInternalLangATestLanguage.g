/*
 * generated by Xtext
 */
grammar DebugInternalLangATestLanguage;

// Rule Main
ruleMain:
	ruleImport
	*
	ruleType
	*
;

// Rule Import
ruleImport:
	'import'
	RULE_STRING
;

// Rule Type
ruleType:
	'type'
	RULE_ID
	(
		'extends'
		RULE_ID
	)?
	(
		'implements'
		RULE_ID
		(
			','
			RULE_ID
		)*
	)?
;

RULE_ID : '^'? ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'_'|'0'..'9')*;

RULE_INT : ('0'..'9')+;

RULE_STRING : ('"' ('\\' .|~(('\\'|'"')))* '"'|'\'' ('\\' .|~(('\\'|'\'')))* '\'');

RULE_ML_COMMENT : '/*' ( options {greedy=false;} : . )*'*/' {skip();};

RULE_SL_COMMENT : '//' ~(('\n'|'\r'))* ('\r'? '\n')? {skip();};

RULE_WS : (' '|'\t'|'\r'|'\n')+ {skip();};

RULE_ANY_OTHER : .;
