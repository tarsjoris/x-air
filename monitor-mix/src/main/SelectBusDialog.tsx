import { Dialog, DialogTitle, List, ListItem, ListItemText, makeStyles } from "@material-ui/core";
import { DefaultTheme } from "styled-components";
import { IScribbleStylesProps, scribbleColorProperties } from "./scribblecolors";
import { IScribbleStripConfig } from './state';

const useScribbleStripStyles = makeStyles<DefaultTheme, IScribbleStylesProps, string>({
    scribble: {
        padding: '4pt',
        ...scribbleColorProperties,
    }
})

interface IBusItemProps {
    key: number,
    busConfig: IScribbleStripConfig,
    onClick: () => void,
}

const BusItem = function (props: IBusItemProps) {
    const { key, busConfig, onClick } = props
    const classes = useScribbleStripStyles({ color: busConfig.color })
    return (
        <ListItem button onClick={onClick} key={key}>
            <ListItemText primary={busConfig.name} className={classes.scribble} />
        </ListItem>
    )
}

export interface ISelectBusDialogProps {
    open: boolean;
    busConfigs: IScribbleStripConfig[];
    selectedBus: number;
    onClose: (bus: number) => void;
}

const useDialogStyles = makeStyles({
    dialog: {
        '& .MuiPaper-root': {
            backgroundColor: '#777777',
        },
        '& .MuiDialogTitle-root': {
            color: '#FFFFFF',
        }
    }
})

const SelectBusDialog = function (props: ISelectBusDialogProps) {
    const { open, busConfigs, selectedBus, onClose } = props
    const classes = useDialogStyles()

    const handleClose = () => {
        onClose(selectedBus);
    }

    const handleListItemClick = (busIndex: number) => {
        onClose(busIndex + 1);
    }

    return (
        <Dialog onClose={handleClose} aria-labelledby="simple-dialog-title" open={open} className={classes.dialog}>
            <DialogTitle id="simple-dialog-title">Choose output bus</DialogTitle>
            <List>
                {busConfigs.map((busConfig, busIndex) => (
                    <BusItem key={busIndex} busConfig={busConfig} onClick={() => handleListItemClick(busIndex)} />
                ))}
            </List>
        </Dialog>
    );
}

export default SelectBusDialog