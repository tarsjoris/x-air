package be.t_ars.xtouch.util

fun <ListenerType, ParamType> partial(
	param: ParamType,
	event: (ListenerType, ParamType) -> Unit
): (ListenerType) -> Unit =
	{ listener -> event(listener, param) }

fun <ListenerType, ParamType1, ParamType2> partial(
	param1: ParamType1,
	param2: ParamType2,
	event: (ListenerType, ParamType1, ParamType2) -> Unit
): (ListenerType) -> Unit =
	{ listener -> event(listener, param1, param2) }

fun <ListenerType, ParamType1, ParamType2, ParamType3> partial(
	param1: ParamType1,
	param2: ParamType2,
	param3: ParamType3,
	event: (ListenerType, ParamType1, ParamType2, ParamType3) -> Unit
): (ListenerType) -> Unit =
	{ listener -> event(listener, param1, param2, param3) }
