
#if (1 == 1)
	do something
#end

#if ($ref > 2 && ${ref} != 5)
	do something 
#end

#if ($velocityCount > 2 && ${velocityCount} != 5)
	do something 
#end

#if ((!$var) && ("$!var" == ""))
	do something 
#end

#if ($var != [] || $var == [])
	do something 
#end