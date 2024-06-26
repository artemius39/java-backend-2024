/*
 * This file is generated by jOOQ.
 */

package edu.java.scrapper.model.jooq;


import edu.java.scrapper.model.jooq.tables.Link;
import edu.java.scrapper.model.jooq.tables.User;
import edu.java.scrapper.model.jooq.tables.UserLink;
import edu.java.scrapper.model.jooq.tables.records.LinkRecord;
import edu.java.scrapper.model.jooq.tables.records.UserLinkRecord;
import edu.java.scrapper.model.jooq.tables.records.UserRecord;
import javax.annotation.processing.Generated;
import org.jooq.ForeignKey;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.Internal;


/**
 * A class modelling foreign key relationships and constraints of tables in the
 * default schema.
 */
@Generated(
    value = {
        "https://www.jooq.org",
        "jOOQ version:3.18.9"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class Keys {

    // -------------------------------------------------------------------------
    // UNIQUE and PRIMARY KEY definitions
    // -------------------------------------------------------------------------

    public static final UniqueKey<LinkRecord> CONSTRAINT_2 = Internal.createUniqueKey(Link.LINK, DSL.name("CONSTRAINT_2"), new TableField[] { Link.LINK.ID }, true);
    public static final UniqueKey<LinkRecord> CONSTRAINT_23 = Internal.createUniqueKey(Link.LINK, DSL.name("CONSTRAINT_23"), new TableField[] { Link.LINK.URL }, true);
    public static final UniqueKey<UserRecord> CONSTRAINT_3 = Internal.createUniqueKey(User.USER, DSL.name("CONSTRAINT_3"), new TableField[] { User.USER.ID }, true);

    // -------------------------------------------------------------------------
    // FOREIGN KEY definitions
    // -------------------------------------------------------------------------

    public static final ForeignKey<UserLinkRecord, UserRecord> CONSTRAINT_B = Internal.createForeignKey(UserLink.USER_LINK, DSL.name("CONSTRAINT_B"), new TableField[] { UserLink.USER_LINK.USER_ID }, Keys.CONSTRAINT_3, new TableField[] { User.USER.ID }, true);
    public static final ForeignKey<UserLinkRecord, LinkRecord> CONSTRAINT_BC = Internal.createForeignKey(UserLink.USER_LINK, DSL.name("CONSTRAINT_BC"), new TableField[] { UserLink.USER_LINK.LINK_ID }, Keys.CONSTRAINT_2, new TableField[] { Link.LINK.ID }, true);
}
