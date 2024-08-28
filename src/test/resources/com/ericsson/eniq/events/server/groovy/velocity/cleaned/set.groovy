
#set($ref = "simple")

#set($ref = [1, 2, "test"])

#set($ref = "$anotherRef")

#set($ref = "$anotherRef.getIt()")

#set($ref = "prefix$anotherRef postfix")

#set($ref = "prefix${anotherRef.getIt()}postfix${yetAnother}")

#set($ref = [1, $ref1, "$ref2", "${ref3}", "prefix${anotherRef.getIt()}postfix${yetAnother}", 4])