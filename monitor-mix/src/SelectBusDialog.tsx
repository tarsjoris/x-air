import { Dialog, DialogTitle, List, ListItem, ListItemText } from "@material-ui/core";
import { IScribbleStripConfig } from './state';

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
                    <ListItem button onClick={() => handleListItemClick(busIndex)} key={busIndex} className={'scribble14'}>
                        <ListItemText primary={busConfig.name} />
                    </ListItem>
                ))}
            </List>
        </Dialog>
    );
}

export default SelectBusDialog