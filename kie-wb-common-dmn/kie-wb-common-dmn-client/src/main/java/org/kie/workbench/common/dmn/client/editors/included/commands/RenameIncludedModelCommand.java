package org.kie.workbench.common.dmn.client.editors.included.commands;

import javax.enterprise.event.Event;

import org.kie.workbench.common.dmn.client.docks.navigator.events.RefreshDecisionComponents;
import org.kie.workbench.common.dmn.client.editors.included.BaseIncludedModelActiveRecord;
import org.kie.workbench.common.dmn.client.editors.included.grid.DMNCardsGridComponent;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.AbstractCanvasCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.CommandResult;

import static org.kie.workbench.common.dmn.client.editors.expressions.util.NameUtils.normaliseName;

public class RenameIncludedModelCommand extends AbstractCanvasCommand {

    private final BaseIncludedModelActiveRecord includedModel;
    private final DMNCardsGridComponent grid;
    private final Event<RefreshDecisionComponents> refreshDecisionComponentsEvent;
    private final String newName;

    private String oldName;

    public RenameIncludedModelCommand(final BaseIncludedModelActiveRecord includedModel,
                                      final DMNCardsGridComponent grid,
                                      final Event<RefreshDecisionComponents> refreshDecisionComponentsEvent,
                                      final String newName) {
        this.includedModel = includedModel;
        this.grid = grid;
        this.refreshDecisionComponentsEvent = refreshDecisionComponentsEvent;
        this.newName = newName;
    }

    public BaseIncludedModelActiveRecord getIncludedModel() {
        return includedModel;
    }

    public DMNCardsGridComponent getGrid() {
        return grid;
    }

    public String getNewName() {
        return newName;
    }

    String getOldName() {
        return oldName;
    }

    void setOldName(final String oldName) {
        this.oldName = oldName;
    }

    @Override
    public CommandResult<CanvasViolation> execute(final AbstractCanvasHandler context) {

        setOldName(getIncludedModel().getName());
        getIncludedModel().setName(normaliseName(getNewName()));

        if (getIncludedModel().isValid()) {
            getIncludedModel().update();
            getGrid().refresh();
            refreshDecisionComponents();
            return CanvasCommandResultBuilder.SUCCESS;
        } else {
            getIncludedModel().setName(getOldName());
            return CanvasCommandResultBuilder.failed();
        }
    }

    @Override
    public CommandResult<CanvasViolation> undo(final AbstractCanvasHandler context) {

        getIncludedModel().setName(getOldName());
        getIncludedModel().update();
        getGrid().refresh();
        refreshDecisionComponents();
        return CanvasCommandResultBuilder.SUCCESS;
    }

    public Event<RefreshDecisionComponents> getRefreshDecisionComponentsEvent() {
        return this.refreshDecisionComponentsEvent;
    }

    void refreshDecisionComponents() {
        getRefreshDecisionComponentsEvent().fire(new RefreshDecisionComponents());
    }
}
