import { CSSProperties } from "@material-ui/styles";

const scribbleColors: CSSProperties[] = [
    /* black */
    {
        backgroundColor: '#000000',
        borderColor: '#FFFFFF',
        color: '#FFFFFF',
    },

    /* red */
    {
        backgroundColor: '#FF0000',
        borderColor: '#FF0000',
        color: '#000000',
    },

    /* green */
    {
        backgroundColor: '#00FF00',
        borderColor: '#00FF00',
        color: '#000000',
    },

    /* yellow */
    {
        backgroundColor: '#FFFF00',
        borderColor: '#FFFF00',
        color: '#000000',
    },

    /* blue */
    {
        backgroundColor: '#0000FF',
        borderColor: '#0000FF',
        color: '#000000',
    },

    /* magenta */
    {
        backgroundColor: '#FF00FF',
        borderColor: '#FF00FF',
        color: '#000000',
    },

    /* cyan */
    {
        backgroundColor: '#00FFFF',
        borderColor: '#00FFFF',
        color: '#000000',
    },

    /* white */
    {
        backgroundColor: '#FFFFFF',
        borderColor: '#FFFFFF',
        color: '#000000',
    },

    /* black inv */
    {
        backgroundColor: '#000000',
        borderColor: '#FFFFFF',
        color: '#FFFFFF',
    },

    /* red inv */
    {
        backgroundColor: '#000000',
        borderColor: '#FF0000',
        color: '#FF0000',
    },

    /* green inv */
    {
        backgroundColor: '#000000',
        borderColor: '#00FF00',
        color: '#00FF00',
    },

    /* yellow inv */
    {
        backgroundColor: '#000000',
        borderColor: '#FFFF00',
        color: '#FFFF00',
    },

    /* blue inv */
    {
        backgroundColor: '#000000',
        borderColor: '#0000FF',
        color: '#0000FF',
    },

    /* magenta inv */
    {
        backgroundColor: '#000000',
        borderColor: '#FF00FF',
        color: '#FF00FF',
    },

    /* cyan inv */
    {
        backgroundColor: '#000000',
        borderColor: '#00FFFF',
        color: '#00FFFF',
    },

    /* white inv */
    {
        backgroundColor: '#000000',
        borderColor: '#FFFFFF',
        color: '#FFFFFF',
    },
]

export interface IScribbleStylesProps {
    color: number
}

export const scribbleColorProperties = ({
    borderWidth: '1pt',
    borderStyle: 'solid',
    backgroundColor: (props: IScribbleStylesProps) => scribbleColors[props.color]?.backgroundColor,
    borderColor: (props: IScribbleStylesProps) => scribbleColors[props.color]?.borderColor,
    color: (props: IScribbleStylesProps) => scribbleColors[props.color]?.color,
})