/*
 * This file is generated by jOOQ.
 */

package edu.java.scrapper.model.jooq.tables.records;


import edu.java.scrapper.model.jooq.tables.UserLink;
import java.beans.ConstructorProperties;
import javax.annotation.processing.Generated;
import org.jetbrains.annotations.NotNull;
import org.jooq.Field;
import org.jooq.Record2;
import org.jooq.Row2;
import org.jooq.impl.TableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "https://www.jooq.org",
        "jOOQ version:3.18.9"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class UserLinkRecord extends TableRecordImpl<UserLinkRecord> implements Record2<Long, Long> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>USER_LINK.USER_ID</code>.
     */
    public void setUserId(@NotNull Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>USER_LINK.USER_ID</code>.
     */
    @jakarta.validation.constraints.NotNull
    @NotNull
    public Long getUserId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>USER_LINK.LINK_ID</code>.
     */
    public void setLinkId(@NotNull Long value) {
        set(1, value);
    }

    /**
     * Getter for <code>USER_LINK.LINK_ID</code>.
     */
    @jakarta.validation.constraints.NotNull
    @NotNull
    public Long getLinkId() {
        return (Long) get(1);
    }

    // -------------------------------------------------------------------------
    // Record2 type implementation
    // -------------------------------------------------------------------------

    @Override
    @NotNull
    public Row2<Long, Long> fieldsRow() {
        return (Row2) super.fieldsRow();
    }

    @Override
    @NotNull
    public Row2<Long, Long> valuesRow() {
        return (Row2) super.valuesRow();
    }

    @Override
    @NotNull
    public Field<Long> field1() {
        return UserLink.USER_LINK.USER_ID;
    }

    @Override
    @NotNull
    public Field<Long> field2() {
        return UserLink.USER_LINK.LINK_ID;
    }

    @Override
    @NotNull
    public Long component1() {
        return getUserId();
    }

    @Override
    @NotNull
    public Long component2() {
        return getLinkId();
    }

    @Override
    @NotNull
    public Long value1() {
        return getUserId();
    }

    @Override
    @NotNull
    public Long value2() {
        return getLinkId();
    }

    @Override
    @NotNull
    public UserLinkRecord value1(@NotNull Long value) {
        setUserId(value);
        return this;
    }

    @Override
    @NotNull
    public UserLinkRecord value2(@NotNull Long value) {
        setLinkId(value);
        return this;
    }

    @Override
    @NotNull
    public UserLinkRecord values(@NotNull Long value1, @NotNull Long value2) {
        value1(value1);
        value2(value2);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached UserLinkRecord
     */
    public UserLinkRecord() {
        super(UserLink.USER_LINK);
    }

    /**
     * Create a detached, initialised UserLinkRecord
     */
    @ConstructorProperties({ "userId", "linkId" })
    public UserLinkRecord(@NotNull Long userId, @NotNull Long linkId) {
        super(UserLink.USER_LINK);

        setUserId(userId);
        setLinkId(linkId);
        resetChangedOnNotNull();
    }

    /**
     * Create a detached, initialised UserLinkRecord
     */
    public UserLinkRecord(edu.java.scrapper.model.jooq.tables.pojos.UserLink value) {
        super(UserLink.USER_LINK);

        if (value != null) {
            setUserId(value.getUserId());
            setLinkId(value.getLinkId());
            resetChangedOnNotNull();
        }
    }
}
