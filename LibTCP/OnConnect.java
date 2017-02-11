/**
 * This interface supports events that are used in Main classes.
 *
 * @author  Miha Bogataj
 * @version 1.0
 * @since   2016-02-11
 */

package LibTCP;

import java.util.HashMap;

public interface OnConnect {
    void onCreate(HashMap<String, String> serverInfo);
    void onConnected();
    void onDisconnected(Object e);
    void onConnectionFailed();
}
