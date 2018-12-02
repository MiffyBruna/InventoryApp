package com.example.android.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.inventoryapp.data.ItemContract;
import com.example.android.inventoryapp.data.ItemContract.ItemEntry;


public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    private static final int EXISTING_ITEM_LOADER = 0;
    int counter;
    int quantity;
    private Uri mCurrentItemUri;
    private EditText mNameEditText;
    private EditText mPriceEditText;
    private EditText mQuantityEditText;
    private EditText mSupplierEditText;
    private EditText mPhoneEditText;
    private Spinner mAvailabilitySpinner;
    private Button increase;
    private Button decrease;
    private Button delete;
    private Button call;
    private String nameString;
    private String priceString;
    private String quantityString;
    private String supplierString;
    private String phoneString;
    private int mAvailability = ItemEntry.AVAILABILITY_UNKNOWN;
    private boolean mItemHasChanged = false;
    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mItemHasChanged = true;
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_editor);
        setTitle(getString(R.string.editor_activity_title_new_item));

        final Intent intent = getIntent();
        mCurrentItemUri = intent.getData();

        increase = findViewById(R.id.increase_button);
        decrease = findViewById(R.id.decrease_button);
        delete = findViewById(R.id.delete_button);
        call = findViewById(R.id.call_supplier);

        if (mCurrentItemUri == null) {
            setTitle(getString(R.string.editor_activity_title_new_item));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_item));
            getLoaderManager().initLoader(EXISTING_ITEM_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mNameEditText = findViewById(R.id.edit_name);
        mPriceEditText = findViewById(R.id.edit_price);
        mQuantityEditText = findViewById(R.id.edit_quantity);
        mSupplierEditText = findViewById(R.id.edit_supplier);
        mPhoneEditText = findViewById(R.id.edit_phone);


        mAvailabilitySpinner = findViewById(R.id.spinner_availability);

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mSupplierEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mPhoneEditText.setOnTouchListener(mTouchListener);
        mAvailabilitySpinner.setOnTouchListener(mTouchListener);
        mQuantityEditText.setText(String.valueOf(0));

        increase.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                fetchCounterInt();
                mQuantityEditText.setText(String.valueOf(counter));
            }
        });

        decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchCounterIntd();
                mQuantityEditText.setText(String.valueOf(counter));
            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = mPhoneEditText.getText().toString();
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                startActivity(intent);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });

        setupSpinner();
    }

    private void fetchCounterInt() {
        String quantity = mQuantityEditText.getText().toString();
        counter = Integer.parseInt(String.valueOf(quantity)) + 1;
    }

    private void fetchCounterIntd() {
        String quantity = mQuantityEditText.getText().toString();
        counter = Integer.parseInt(String.valueOf(quantity)) - 1;

        if (counter <= 0) {
            counter = 0;
            mQuantityEditText.setText(String.valueOf(counter));
            mAvailability = ItemEntry.QUANTITY_OUTSTOCK;
        }
    }

    private void setupSpinner() {

        ArrayAdapter availabilitySpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.array_quantity_options, android.R.layout.simple_spinner_item);

        availabilitySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        mAvailabilitySpinner.setAdapter(availabilitySpinnerAdapter);

        mAvailabilitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.quantity_instock))) {
                        mAvailability = ItemEntry.QUANTITY_INSTOCK;
                    }
                    if (selection.equals(getString(R.string.quantity_nostock))) {
                        mQuantityEditText.setText("0");
                        mAvailability = ItemEntry.QUANTITY_OUTSTOCK;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mAvailability = ItemEntry.AVAILABILITY_UNKNOWN;
            }
        });
    }

    private void saveItem() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        nameString = mNameEditText.getText().toString().trim();
        priceString = mPriceEditText.getText().toString().trim();
        quantityString = mQuantityEditText.getText().toString().trim();
        supplierString = mSupplierEditText.getText().toString().trim();
        phoneString = mPhoneEditText.getText().toString().trim();


        try {
            quantity = Integer.parseInt(quantityString);
        } catch (Exception e) {
            quantity = 0;
        }


        if (!currentItemData(nameString, quantityString, priceString, supplierString, phoneString)) {
                return;
            }
            // Create a ContentValues object where column names are the keys,
            ContentValues values = new ContentValues();
            values.put(ItemEntry.COLUMN_ITEM_NAME, nameString);
            values.put(ItemEntry.COLUMN_ITEM_PRICE, priceString);
            values.put(ItemEntry.COLUMN_ITEM_QUANTITY, quantityString);
            values.put(ItemEntry.COLUMN_ITEM_SUPPLIER, supplierString);
            values.put(ItemEntry.COLUMN_ITEM_AVAILABILITY, mAvailability);
            values.put(ItemEntry.COLUMN_ITEM_PHONE, phoneString);

            // Determine if this is a new or existing item by checking if itemUri is null or not
            if (mCurrentItemUri == null) {

                Uri newUri = getContentResolver().insert(ItemContract.ItemEntry.CONTENT_URI, values);
                // Show a toast message depending on whether or not the insertion was successful.
                if (newUri == null) {
                    // If the new content URI is null, then there was an error with insertion.
                    Toast.makeText(this, getString(R.string.editor_insert_item_failed), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.editor_insert_item_successful), Toast.LENGTH_SHORT).show();
                    finish();
                }
            } else {

                int rowsAffected = getContentResolver().update(mCurrentItemUri, values, null, null);
                if (!currentItemData(nameString, quantityString, priceString, supplierString, phoneString)) {
                    // Since no fields were modified, we can return early without creating a new book.
                    // No need to create ContentValues and no need to do any ContentProvider operations.
                    return;
                }
                // Show a toast message depending on whether or not the update was successful.
                if (rowsAffected == 0) {
                    // If no rows were affected, then there was an error with the update.
                    Toast.makeText(this, getString(R.string.editor_update__item_failed), Toast.LENGTH_SHORT).show();
                } else {
                    // Otherwise, the update was successful and we can display a toast.
                    Toast.makeText(this, getString(R.string.editor_update_item_successful), Toast.LENGTH_SHORT).show();

                }
            }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new item, hide the "Delete" menu item.
        if (mCurrentItemUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_save:
                saveItem();
                return true;

            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;

            case android.R.id.home:
                if (!mItemHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, navigate to parent activity.
                        NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    }
                };
                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        if (!mItemHasChanged) {
            super.onBackPressed();
            return;
        } else {
        }
        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.

        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // User clicked "Discard" button, close the current activity.
                finish();
            }
        };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {ItemEntry._ID, ItemEntry.COLUMN_ITEM_NAME, ItemEntry.COLUMN_ITEM_QUANTITY, ItemEntry.COLUMN_ITEM_PRICE, ItemEntry.COLUMN_ITEM_AVAILABILITY, ItemEntry.COLUMN_ITEM_PHONE, ItemEntry.COLUMN_ITEM_SUPPLIER,};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this, mCurrentItemUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {

            int nameColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_NAME);
            int priceColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_QUANTITY);
            int availabilityColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_AVAILABILITY);
            int supplierColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_SUPPLIER);
            int phoneColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_PHONE);

            String name = cursor.getString(nameColumnIndex);
            String supplier = cursor.getString(supplierColumnIndex);

            int availability = cursor.getInt(availabilityColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int phone = cursor.getInt(phoneColumnIndex);


            // Update the views on the screen with the values from the database
            mNameEditText.setText(name);
            mPriceEditText.setText(Integer.toString(price));
            mQuantityEditText.setText(Integer.toString(quantity));
            mSupplierEditText.setText(supplier);
            mPhoneEditText.setText(Integer.toString(phone));

            // Gender is a dropdown spinner, so map the constant value from the database
            // Then call setSelection() so that option is displayed on screen as the current selection.


            switch (availability) {
                case ItemEntry.QUANTITY_INSTOCK:
                    mAvailabilitySpinner.setSelection(1);
                    break;
                case ItemEntry.QUANTITY_OUTSTOCK:
                    mAvailabilitySpinner.setSelection(2);
                    break;
                default:
                    mAvailabilitySpinner.setSelection(0);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");
        mSupplierEditText.setText("");
        mPriceEditText.setText("");
        mPhoneEditText.setText("");
        mQuantityEditText.setText("");
        mAvailabilitySpinner.setSelection(0);
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor
     */
    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteItem();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void deleteItem() {
        // Only perform the delete if this is an existing item
        if (mCurrentItemUri != null) {

            int rowsDeleted = getContentResolver().delete(mCurrentItemUri, null, null);

            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_item_failed), Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_item_successful), Toast.LENGTH_SHORT).show();
            }
        }
        // Close the activity//
        finish();
    }

    private boolean currentItemData(String nameString, String quantity, String price, String supplierString, String phoneString) {
        if (TextUtils.isEmpty(nameString)) {
            Toast.makeText(this, getResources().getString(R.string.name_required), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(quantity)) {
            Toast.makeText(this, getResources().getString(R.string.quantity_required), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(supplierString)) {
            Toast.makeText(this, getResources().getString(R.string.supplier_required), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(price)) {
            Toast.makeText(this, getResources().getString(R.string.price_required), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(phoneString)) {
            Toast.makeText(this, getResources().getString(R.string.phone_required), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}