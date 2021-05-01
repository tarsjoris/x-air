import { Dialog, DialogTitle, List, ListItem, ListItemText, makeStyles } from "@material-ui/core";
import { DefaultTheme } from "styled-components";
import { IScribbleStylesProps, scribbleColorProperties } from "./scribblecolors";
import { IScribbleStripConfig } from './state';

const useScribbleStripStyles = makeStyles<DefaultTheme, IScribbleStylesProps, string>({
    scribble: scribbleColorProperties,
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
        <ListItem button onClick={onClick} key={key} className={classes.scribble}>
            <ListItemText primary={busConfig.name} />
        </ListItem>
    )
}

export interface ISelectBusDialogProps {
    open: boolean;
    busConfigs: IScribbleStripConfig[];
    selectedBus: number;
    onClose: (bus: number) => void;
}

const SelectBusDialog = function (props: ISelectBusDialogProps) {
    const { open, busConfigs, selectedBus, onClose } = props

    const handleClose = () => {
        onClose(selectedBus);
    }

    const handleListItemClick = (busIndex: number) => {
        onClose(busIndex);
    }

    return (
        <Dialog onClose={handleClose} aria-labelledby="simple-dialog-title" open={open}>
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