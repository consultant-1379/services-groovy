
#if (1 == 2)
	do something
#elseif ($ref > 3)
	do something different
#end

#if (1 == 2)
	do something
#elseif ($ref > 3)
	do something different
#elseif ($ref > 3 && $var != "3")
	do something different
#end