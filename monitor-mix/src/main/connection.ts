import { Dispatch } from 'react';
import ReconnectingWebSocket from 'reconnecting-websocket';
import { IAction } from "./state"

const processBusMessage = function (commandParts: string[], args: string[], dispatch: (action: IAction) => void) {
    if (commandParts.length > 1) {
        switch (commandParts[1]) {
            case "name": {
                const bus = Number(args[0])
                dispatch({ command: "busname", bus, name: args[1] })
                break
            }
            case "color": {
                const bus = Number(args[0])
                dispatch({ command: "buscolor", bus, color: Number(args[1]) })
                break
            }
            case "level": {
                dispatch({ command: "buslevel", level: Number(args[0]) })
                break
            }
        }
    }
}

const processChannelMessage = function (commandParts: string[], args: string[], dispatch: (action: IAction) => void) {
    if (commandParts.length > 1) {
        const channel = Number(args[0])
        switch (commandParts[1]) {
            case "name": {
                dispatch({ command: "channelname", channel, name: args[1] })
                break
            }
            case "color": {
                dispatch({ command: "channelcolor", channel, color: Number(args[1]) })
                break
            }
            case "level": {
                dispatch({ command: "channellevel", channel, level: Number(args[1]) })
                break
            }
        }
    }
}

const processMessage = function (message: string, dispatch: (action: IAction) => void) {
    const parts = message.split("|")
    if (parts.length > 0) {
        const commandParts = parts[0].split("/")
        const args = parts.slice(1)
        if (commandParts.length > 0) {
            switch (commandParts[0]) {
                case "bus": {
                    processBusMessage(commandParts, args, dispatch)
                    break
                }
                case "ch": {
                    processChannelMessage(commandParts, args, dispatch)
                    break
                }
            }
        }
    }
}

const processMessages = function (data: string, dispatch: (action: IAction) => void) {
    data.split(";")
        .forEach(message => processMessage(message, dispatch))
}

export class Connection {
    client: ReconnectingWebSocket
    dispatch: Dispatch<IAction> | null = null
    selectedBus: number = 1

    constructor() {
        const hostname = window.location.hostname
        this.client = new ReconnectingWebSocket(`ws://${hostname}:8080/relay/monitor-mix`);
        this.client.onopen = () => {
            console.log('WebSocket Client Connected')
            this.requestBusParams()
        }
        this.client.onmessage = (message: MessageEvent<string>) => {
            if (this.dispatch != null) {
                processMessages(message.data, this.dispatch)
            }
        }
        this.client.onclose = (message) => {
            console.log("Connection closed")
        }
    }

    init = (dispatch: Dispatch<IAction>, selectedBus: number) => {
        this.dispatch = dispatch
        this.selectBus(selectedBus)
    }

    clearDispatch = () => {
        this.dispatch = null
    }

    selectBus = (selectedBus: number) => {
        this.selectedBus = selectedBus
        this.requestBusParams()
    }

    setChannelLevel = (channel: number, level: number) => {
        this.client.send(`channel|${channel}|${level}`)
    }

    private requestBusParams = () => {
        this.client.send(`select|${this.selectedBus}`)
    }
}