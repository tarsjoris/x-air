import { makeStyles, Slider, Typography, withStyles } from "@material-ui/core"
import { useCallback } from "react"
import { DefaultTheme } from "styled-components"
import { IScribbleStylesProps, scribbleColorProperties } from "./scribblecolors"
import { IChannelConfig } from "./state"

interface IScribleStripProps {
    color: number,
    label: string
}

const useScribbleStripStyles = makeStyles<DefaultTheme, IScribbleStylesProps, string>({
    scribble: {
        width: '65pt',
        height: '35pt',
        padding: '1pt',
        borderRadius: '2pt',
        textAlign: 'center',
        wordWrap: 'break-word',
        ...scribbleColorProperties,
    }
})

const ScribbleStrip = function (props: IScribleStripProps) {
    const classes = useScribbleStripStyles({ color: props.color })
    return <Typography className={classes.scribble}>{props.label}</Typography >
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
})(Slider)

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
    const { onChange, level } = props
    const onChangeCallback = useCallback((_, value) => onChange(value), [onChange])
    return (
        <div style={{ marginLeft: 25, marginRight: 25 }}>
            <Fader
                min={0}
                max={80}
                value={level}
                onChange={onChangeCallback}
                marks={marks} />
        </div>
    )
}

export interface IChannelProps {
    channelConfig: IChannelConfig,
    onChange: (value: number) => void
}

export const Channel = function (props: IChannelProps) {
    const { channelConfig, onChange } = props
    const { color, name, level } = channelConfig
    return (
        <div>
            <div style={{ float: 'left', paddingBottom: '15pt' }}>
                <ScribbleStrip color={color} label={name} />
            </div>
            <div style={{ marginLeft: '75pt', paddingTop: '8pt', paddingBottom: '7pt' }}>
                <ChannelFader level={level} onChange={onChange} />
            </div>
        </div>
    )
}