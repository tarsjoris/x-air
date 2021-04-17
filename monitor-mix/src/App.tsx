import React, { useCallback, useEffect, useReducer } from 'react';
import { AppBar, IconButton, Slider, Toolbar, Typography } from '@material-ui/core';
import { createStyles, makeStyles, Theme, withStyles } from '@material-ui/core/styles';
import MenuIcon from '@material-ui/icons/Menu';
import './index.css';
import { IChannelConfig, IScribbleStripConfig, reducer, initialState } from './state';
import { createConnection } from './connection';


const useStyles = makeStyles((theme: Theme) =>
	createStyles({
		root: {
			flexGrow: 1,
			backgroundColor: "#444444",
		},
		menuButton: {
			marginRight: theme.spacing(2),
		},
		title: {
			flexGrow: 1,
		},
	}),
);

interface ITopBarProps {
	busConfig?: IScribbleStripConfig
}

const TopBar = function (props: ITopBarProps) {
	const classes = useStyles();
	return (
		<div className={classes.root}>
			<AppBar position="sticky">
				<Toolbar>
					<IconButton edge="start" className={classes.menuButton} color="inherit" aria-label="menu">
						<MenuIcon />
					</IconButton>
					<Typography variant="h6" className={classes.title}>
						{props.busConfig?.name ?? "Monitor Mix"}
					</Typography>
				</Toolbar>
			</AppBar>
		</div>
	)
}

interface IScribleStripProps {
	color: number,
	label: string
}

const ScribbleStrip = function (props: IScribleStripProps) {
	return <Typography >{props.label}</Typography >
}

const Fader = withStyles({
	thumb: {
		height: 48,
		width: 25,
		backgroundImage: 'url("fader-button.png")',
		marginTop: -24,
		marginLeft: -13,
		borderRadius: 0,
		transform: 'rotate(90deg)'
	},
	mark: {
		height: 30,
		marginTop: -15
	},
	markActive: {
		backgroundColor: 'rgb(132, 134, 136)'
	},
	markLabel: {
		color: 'rgb(132, 134, 136)',
	}
})(Slider);

const marks = [
	{ value: 10, label: '50' },
	{ value: 20, label: '30' },
	{ value: 30, label: '20' },
	{ value: 40, label: '10' },
	{ value: 50, label: '5' },
	{ value: 60, label: '0' },
	{ value: 70, label: '5' },
	{ value: 80, label: '10' },
]

interface IChannelFaderProps {
	level: number,
	onChange: (value: number) => void
}

const ChannelFader = function (props: IChannelFaderProps) {
	const onChange = useCallback((_, value) => props.onChange(value), [props])
	return (
		<div style={{ marginLeft: 25, marginRight: 25 }}>
			<Fader
				min={0}
				max={80}
				value={props.level}
				onChange={onChange}
				marks={marks} />
		</div>
	)
}

interface IChannelProps {
	channelConfig: IChannelConfig,
	onChange: (value: number) => void
}

const Channel = function (props: IChannelProps) {
	return (
		<div style={{ marginTop: 20, marginBottom: 20 }}>
			<div style={{ float: 'left', width: 90 }}>
				<ScribbleStrip color={props.channelConfig.color} label={props.channelConfig.name} />
			</div>
			<div style={{ marginLeft: 90 }}>
				<ChannelFader level={props.channelConfig.level} onChange={props.onChange} />
			</div>
		</div>
	)
}

interface IChannelFadersProps {
	channelConfigs: IChannelConfig[]
	setChannelLevel: (channel: number, level: number) => void
}


const ChannelFaders = function (props: IChannelFadersProps) {
	return (
		<div style={{ margin: '1em' }}>
			{props.channelConfigs.map((config, index) => (
				<Channel key={index} channelConfig={config} onChange={value => props.setChannelLevel(index + 1, value)} />
			))}
		</div>
	)
}

const App = function () {
	const [state, dispatch] = useReducer(reducer, initialState);
	useEffect(() => {
		createConnection(dispatch)
	}, [dispatch])
	const setChannelLevel = useCallback((channel: number, level: number) =>
		dispatch({ command: "channellevel", channel, level }),
		[dispatch])
	return (
		<>
			<TopBar busConfig={state.busConfigs[state.selectedBus - 1]} />
			<ChannelFaders channelConfigs={state.channelConfigs} setChannelLevel={setChannelLevel} />
		</>
	)
}

export default App;