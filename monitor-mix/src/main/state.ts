export interface IScribbleStripConfig {
    name: string,
    color: number
}

export interface IChannelConfig extends IScribbleStripConfig {
    level: number
}

export interface IState {
    selectedBus: number,
    busConfigs: IScribbleStripConfig[]
    busLevel: number
    channelConfigs: IChannelConfig[]
}

export const initialState: IState = {
    selectedBus: 1,
    busConfigs: [...new Array(6)].map((_, index) => (
        {
            name: `Bus ${index + 1}`,
            color: 0
        })),
    busLevel: 0,
    channelConfigs: [...new Array(17)].map((_, index) => (
        {
            name: `Channel ${index + 1}`,
            color: 0,
            level: 0
        }))
}

export interface IBusNameAction {
    command: "busname",
    bus: number,
    name: string
}

export interface IBusColorAction {
    command: "buscolor",
    bus: number,
    color: number
}

export interface IBusLevelAction {
    command: "buslevel",
    level: number
}

export interface IChannelNameAction {
    command: "channelname",
    channel: number,
    name: string
}

export interface IChannelColorAction {
    command: "channelcolor",
    channel: number,
    color: number
}

export interface IChannelLevelAction {
    command: "channellevel",
    channel: number,
    level: number
}

export interface ISelectBusAction {
    command: "selectbus",
    bus: number,
}

export type IAction = IBusNameAction | IBusColorAction | IBusLevelAction | IChannelNameAction | IChannelColorAction | IChannelLevelAction | ISelectBusAction

export const reducer = function (state: IState, action: IAction): IState {
    switch (action.command) {
        case "busname": {
            return {
                ...state,
                busConfigs: [
                    ...state.busConfigs.slice(0, action.bus - 1),
                    {
                        ...state.busConfigs[action.bus - 1],
                        name: action.name,
                    },
                    ...state.busConfigs.slice(action.bus),
                ],
            }
        }
        case "buscolor": {
            return {
                ...state,
                busConfigs: [
                    ...state.busConfigs.slice(0, action.bus - 1),
                    {
                        ...state.busConfigs[action.bus - 1],
                        color: action.color,
                    },
                    ...state.busConfigs.slice(action.bus),
                ],
            }
        }
        case "buslevel": {
            return {
                ...state,
                busLevel: action.level,
            }
        }
        case "channelname": {
            return {
                ...state,
                channelConfigs: [
                    ...state.channelConfigs.slice(0, action.channel - 1),
                    {
                        ...state.channelConfigs[action.channel - 1],
                        name: action.name,
                    },
                    ...state.channelConfigs.slice(action.channel),
                ],
            }
        }
        case "channelcolor": {
            return {
                ...state,
                channelConfigs: [
                    ...state.channelConfigs.slice(0, action.channel - 1),
                    {
                        ...state.channelConfigs[action.channel - 1],
                        color: action.color,
                    },
                    ...state.channelConfigs.slice(action.channel),
                ],
            }
        }
        case "channellevel": {
            return {
                ...state,
                channelConfigs: [
                    ...state.channelConfigs.slice(0, action.channel - 1),
                    {
                        ...state.channelConfigs[action.channel - 1],
                        level: action.level,
                    },
                    ...state.channelConfigs.slice(action.channel),
                ],
            }
        }
        case "selectbus": {
            return {
                ...state,
                selectedBus: action.bus,
            }
        }
        default: {
            return state
        }
    }
}
