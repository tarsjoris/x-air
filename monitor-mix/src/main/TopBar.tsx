import { makeStyles, Typography } from "@material-ui/core"
import { useState } from "react"
import { DefaultTheme } from "styled-components"
import { IScribbleStylesProps, scribbleColorProperties } from "./scribblecolors"
import SelectBusDialog from "./SelectBusDialog"
import { IScribbleStripConfig } from "./state"

const useTopBarStyles = makeStyles<DefaultTheme, IScribbleStylesProps, string>({
    header: {
        top: 0,
        left: 'auto',
        right: 0,
        position: 'sticky',
        width: '100%',
        display: 'flex',
        zIndex: 1100,
        boxSizing: 'border-box',
        flexDirection: 'column',
    },
    headerTitle: {
        minHeight: '64px',
        paddingLeft: '24px',
        paddingRight: '24px',
        display: 'flex',
        position: 'relative',
        alignItems: 'center',
    },
    scribble: scribbleColorProperties,
})


export interface ITopBarProps {
    busConfigs: IScribbleStripConfig[],
    selectedBus: number,
    selectBus: (bus: number) => void,
}

export const TopBar = function (props: ITopBarProps) {
    const { busConfigs, selectedBus, selectBus } = props
    const [dialogOpen, setDialogOpen] = useState(false)
    const busConfig = busConfigs[selectedBus - 1]
    const color = busConfig?.color ?? 0
    const classes = useTopBarStyles({ color })

    const handleOpenDialog = () => {
        setDialogOpen(true)
    }

    const handleCloseDialog = (bus: number) => {
        setDialogOpen(false)
        selectBus(bus)
    }
    return (
        <>
            <header className={`${classes.header} ${classes.scribble}`} onClick={handleOpenDialog}>
                <div className={classes.headerTitle}>
                    <Typography variant="h6">
                        {busConfig?.name ?? "Monitor Mix"}
                    </Typography>
                </div>
            </header>
            <SelectBusDialog open={dialogOpen} busConfigs={busConfigs} selectedBus={selectedBus} onClose={handleCloseDialog} />
        </>
    )
}
