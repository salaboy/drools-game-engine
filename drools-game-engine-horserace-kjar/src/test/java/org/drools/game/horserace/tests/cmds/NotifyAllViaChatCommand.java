
package org.drools.game.horserace.tests.cmds;

import org.drools.game.core.api.BaseCommand;
import org.drools.game.core.api.Context;
import org.drools.game.model.api.Player;

public class NotifyAllViaChatCommand extends BaseCommand<Void> {

    private String message;

    public NotifyAllViaChatCommand( Player player, String message ) {
        super( player );
        this.message = message;
    }

    @Override
    public Void execute( Context ctx ) {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    public String getMessage() {
        return message;
    }

    public void setMessage( String message ) {
        this.message = message;
    }

}