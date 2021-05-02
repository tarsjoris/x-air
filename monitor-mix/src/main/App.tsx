import { useCallback, useEffect, useReducer } from 'react';
import './index.css';
import { IChannelConfig, reducer, initialState } from './state';
import { Connection } from './connection';
import { TopBar } from './TopBar';
import { Channel } from './Channel';


interface IChannelFadersProps {
	channelConfigs: IChannelConfig[]
	setChannelLevel: (channel: number, level: number) => void
}


const ChannelFaders = function (props: IChannelFadersProps) {
	const { channelConfigs, setChannelLevel } = props
	return (
		<div style={{ margin: '1em' }}>
			{channelConfigs.map((config, index) => (
				<Channel key={index} channelConfig={config} onChange={value => setChannelLevel(index + 1, value)} />
			))}
		</div>
	)
}

const connection = new Connection()

const App = function () {
	const [state, dispatch] = useReducer(reducer, initialState);
	const selectedBus = state.selectedBus
	useEffect(() => {
		connection.init(dispatch, selectedBus)
		return connection.clearDispatch
	}, [dispatch, selectedBus])

	const selectBus = useCallback((bus: number) => {
		window.localStorage.setItem("bus", bus.toString())
		connection.selectBus(bus)
		dispatch({ command: "selectbus", bus })
	}, [dispatch])
	return (
		<>
			<TopBar busConfigs={state.busConfigs} selectedBus={selectedBus} selectBus={selectBus} />
			<ChannelFaders channelConfigs={state.channelConfigs} setChannelLevel={connection.setChannelLevel} />
		</>
	)
}

export default App