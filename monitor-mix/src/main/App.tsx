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
	useEffect(() => {
		connection.setDispatch(dispatch)
		return connection.clearDispatch
	}, [dispatch])

	const selectBus = useCallback((bus: number) => {
		connection.selectBus(bus)
		dispatch({ command: "selectbus", bus })
	}, [dispatch])
	return (
		<>
			<TopBar busConfigs={state.busConfigs} selectedBus={state.selectedBus} selectBus={selectBus} />
			<ChannelFaders channelConfigs={state.channelConfigs} setChannelLevel={connection.setChannelLevel} />
		</>
	)
}

export default App