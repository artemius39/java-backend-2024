/*
 * This file is generated by jOOQ.
 */

package edu.java.scrapper.model.jooq;


import edu.java.scrapper.model.jooq.tables.Link;
import edu.java.scrapper.model.jooq.tables.User;
import edu.java.scrapper.model.jooq.tables.UserLink;
import javax.annotation.processing.Generated;


/**
 * Convenience access to all tables in the default schema.
 */
@Generated(
    value = {
        "https://www.jooq.org",
        "jOOQ version:3.18.9"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class Tables {

    /**
     * The table <code>LINK</code>.
     */
    public static final Link LINK = Link.LINK;

    /**
     * The table <code>user</code>.
     */
    public static final User USER = User.USER;

    /**
     * The table <code>USER_LINK</code>.
     */
    public static final UserLink USER_LINK = UserLink.USER_LINK;
}
