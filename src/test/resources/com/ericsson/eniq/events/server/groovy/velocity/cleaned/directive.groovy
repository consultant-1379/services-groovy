
#DIRECTIVE_WITH_NO_PARAMS

#DIRECTIVE_WITH_NO_PARAMS_AND_PARENTHESIS()

#DIRECTIVE_WITH_SIMPlE_PARAMS("param1" 2 3)

#DIRECTIVE_WITH_REF_PARAMS("$ref1" $ref2 ${ref3} "$ref4.getIt()" "prefix${ref5.getIt()}postfix$ref6")

#DIRECTIVE_WITH_REF_ARRAY_PARAMS("param1" ["$ref1", $ref2, ${ref3}, "$ref4.getIt()", "prefix${ref5.getIt()}postfix$ref6"])

#DIRECTIVE_WITH_VELOCITY_COUNT($velocityCount "$velocityCount")

#DIRECTIVE_WITH_MIXED_PARAMS("param1" ["$ref1", "prefix${ref1.getIt()}postfix", 3] $ref5 [3, "4"])
