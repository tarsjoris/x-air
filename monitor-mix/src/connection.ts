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

export const createConnection = function (dispatch: (action: IAction) => void) {
    const hostname = window.location.hostname
    const client = new ReconnectingWebSocket(`ws://${hostname}:8080/relay/monitor-mix`);
    client.onopen = () => {
        console.log('WebSocket Client Connected');
        client.send("select|1");
    };
    client.onmessage = (message: MessageEvent<string>) => {
        processMessages(message.data, dispatch);
    }
    client.onclose = (message) => {
        console.log("Connection closed")
    }
    return client.close
}
