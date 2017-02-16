package com.caoping.cloud.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import com.caoping.cloud.entiy.ShoppingCart;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;


// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * DAO for table SHOPPING_CART.
*/
public class ShoppingCartDao extends AbstractDao<ShoppingCart, String> {

    public static final String TABLENAME = "SHOPPING_CART";

    /**
     * Properties of entity ShoppingCart.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Cartid = new Property(0, String.class, "cartid", true, "CARTID");
        public final static Property Goods_id = new Property(1, String.class, "goods_id", false, "GOODS_ID");
        public final static Property Emp_id = new Property(2, String.class, "emp_id", false, "EMP_ID");
        public final static Property Manager_id = new Property(3, String.class, "manager_id", false, "MANAGER_ID");
        public final static Property Emp_name = new Property(4, String.class, "emp_name", false, "EMP_NAME");
        public final static Property Emp_cover = new Property(5, String.class, "emp_cover", false, "EMP_COVER");
        public final static Property Goods_name = new Property(6, String.class, "goods_name", false, "GOODS_NAME");
        public final static Property Goods_cover = new Property(7, String.class, "goods_cover", false, "GOODS_COVER");
        public final static Property Sell_price = new Property(8, String.class, "sell_price", false, "SELL_PRICE");
        public final static Property MarketPrice = new Property(9, String.class, "marketPrice", false, "MARKET_PRICE");
        public final static Property Goods_count = new Property(10, String.class, "goods_count", false, "GOODS_COUNT");
        public final static Property Dateline = new Property(11, String.class, "dateline", false, "DATELINE");
        public final static Property Is_zhiying = new Property(12, String.class, "is_zhiying", false, "IS_ZHIYING");
        public final static Property Is_select = new Property(13, String.class, "is_select", false, "IS_SELECT");
        public final static Property Pv_prices = new Property(14, String.class, "pv_prices", false, "PV_PRICES");
    };

    private DaoSession daoSession;


    public ShoppingCartDao(DaoConfig config) {
        super(config);
    }
    
    public ShoppingCartDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'SHOPPING_CART' (" + //
                "'CARTID' TEXT PRIMARY KEY NOT NULL ," + // 0: cartid
                "'GOODS_ID' TEXT NOT NULL ," + // 1: goods_id
                "'EMP_ID' TEXT," + // 2: emp_id
                "'MANAGER_ID' TEXT," + // 3: manager_id
                "'EMP_NAME' TEXT," + // 4: emp_name
                "'EMP_COVER' TEXT," + // 5: emp_cover
                "'GOODS_NAME' TEXT," + // 6: goods_name
                "'GOODS_COVER' TEXT," + // 7: goods_cover
                "'SELL_PRICE' TEXT," + // 8: sell_price
                "'MARKET_PRICE' TEXT," + // 9: marketPrice
                "'GOODS_COUNT' TEXT," + // 10: goods_count
                "'DATELINE' TEXT," + // 11: dateline
                "'IS_ZHIYING' TEXT," + // 12: is_zhiying
                "'IS_SELECT' TEXT," +
                "'PV_PRICES' TEXT);"); // 13: is_select
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'SHOPPING_CART'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, ShoppingCart entity) {
        stmt.clearBindings();
        stmt.bindString(1, entity.getCartid());
        stmt.bindString(2, entity.getGoods_id());
 
        String emp_id = entity.getEmp_id();
        if (emp_id != null) {
            stmt.bindString(3, emp_id);
        }
 
        String manager_id = entity.getManager_id();
        if (manager_id != null) {
            stmt.bindString(4, manager_id);
        }
 
        String emp_name = entity.getEmp_name();
        if (emp_name != null) {
            stmt.bindString(5, emp_name);
        }
 
        String emp_cover = entity.getEmp_cover();
        if (emp_cover != null) {
            stmt.bindString(6, emp_cover);
        }
 
        String goods_name = entity.getGoods_name();
        if (goods_name != null) {
            stmt.bindString(7, goods_name);
        }
 
        String goods_cover = entity.getGoods_cover();
        if (goods_cover != null) {
            stmt.bindString(8, goods_cover);
        }
 
        String sell_price = entity.getSell_price();
        if (sell_price != null) {
            stmt.bindString(9, sell_price);
        }
 
        String marketPrice = entity.getMarketPrice();
        if (marketPrice != null) {
            stmt.bindString(10, marketPrice);
        }
 
        String goods_count = entity.getGoods_count();
        if (goods_count != null) {
            stmt.bindString(11, goods_count);
        }
 
        String dateline = entity.getDateline();
        if (dateline != null) {
            stmt.bindString(12, dateline);
        }
 
        String is_zhiying = entity.getIs_zhiying();
        if (is_zhiying != null) {
            stmt.bindString(13, is_zhiying);
        }
 
        String is_select = entity.getIs_select();
        if (is_select != null) {
            stmt.bindString(14, is_select);
        }

        String pv_prices = entity.getPv_prices();
        if (pv_prices != null) {
            stmt.bindString(15, pv_prices);
        }
    }

    @Override
    protected void attachEntity(ShoppingCart entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    /** @inheritdoc */
    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.getString(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public ShoppingCart readEntity(Cursor cursor, int offset) {
        ShoppingCart entity = new ShoppingCart( //
            cursor.getString(offset + 0), // cartid
            cursor.getString(offset + 1), // goods_id
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // emp_id
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // manager_id
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // emp_name
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // emp_cover
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // goods_name
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // goods_cover
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // sell_price
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // marketPrice
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // goods_count
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11), // dateline
            cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12), // is_zhiying
            cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13), // is_select
            cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14) // pv_prices
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, ShoppingCart entity, int offset) {
        entity.setCartid(cursor.getString(offset + 0));
        entity.setGoods_id(cursor.getString(offset + 1));
        entity.setEmp_id(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setManager_id(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setEmp_name(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setEmp_cover(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setGoods_name(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setGoods_cover(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setSell_price(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setMarketPrice(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setGoods_count(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setDateline(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
        entity.setIs_zhiying(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
        entity.setIs_select(cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13));
        entity.setPv_prices(cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14));
     }
    
    /** @inheritdoc */
    @Override
    protected String updateKeyAfterInsert(ShoppingCart entity, long rowId) {
        return entity.getCartid();
    }
    
    /** @inheritdoc */
    @Override
    public String getKey(ShoppingCart entity) {
        if(entity != null) {
            return entity.getCartid();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
