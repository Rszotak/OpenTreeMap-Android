package org.azavea.otm.ui;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

import org.azavea.otm.App;
import org.azavea.otm.R;
import org.azavea.otm.data.PendingEdit;
import org.azavea.otm.data.PendingEditDescription;
import org.azavea.otm.data.Plot;
import org.azavea.otm.data.User;
import org.azavea.otm.data.UserType;
import org.azavea.otm.rest.RequestGenerator;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.JsonHttpResponseHandler;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import org.azavea.otm.Field;

public class PendingItemDisplay extends Activity {
	Plot plot;
	String key;
	String label;
	CheckBox selectedValue;
	CheckBox currentValue;
	Vector<CheckBox> allPending = new Vector();
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_item);
        
        this.key = getIntent().getStringExtra("key");
        this.label = getIntent().getStringExtra("label");
       
        plot = new Plot();
		try {
			plot.setData(new JSONObject(getIntent().getStringExtra("plot")));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			render();
		} catch (JSONException e) {
			e.printStackTrace();
			Toast.makeText(this, "Error rendering pending edit.", Toast.LENGTH_SHORT).show();
		}
		
		Log.d("PENDING", "key: " + key);
		Log.d("PENDING", "label: " + label);
		
		
		
    }
	
	//TODO this code belongs in User.
	public boolean canApprovePendingEdits() {
		User u = App.getLoginManager().loggedInUser;
		try {
			if (u != null) {
				UserType t = u.getUserType();
				int userLevel = t.getLevel();
				if (userLevel > User.ADMINISTRATOR_LEVEL) {
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	private void renderCurrentValue() {
		View row = getLayoutInflater().inflate(R.layout.pending_edit_row, null);
		ViewGroup container = (ViewGroup) findViewById(R.id.currentValue);
		Object value = null;
		boolean CURRENT_ONLY = true;
		try {
			value = plot.get(CURRENT_ONLY, key);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		String v;
		if (value != null  && !value.equals("")) {
			v = value.toString();
			container.addView(row);
		} else {
			v = "No current value";
		}
		((TextView)row.findViewById(R.id.value)).setText(v);
		((TextView)row.findViewById(R.id.user_name)).setText("");
		((TextView)row.findViewById(R.id.date)).setText("");
		
		currentValue = (CheckBox)row.findViewById(R.id.checkBox);
		currentValue.setOnClickListener(checkBoxClickListener);
		
	}
	
	private void renderPendingValues() throws JSONException {
		ViewGroup container = (ViewGroup) findViewById(R.id.pendingEdits);
		PendingEditDescription pendingEditDescription = plot.getPendingEditForKey(key);
		List<PendingEdit> pendingEdits = pendingEditDescription.getPendingEdits();
		for (PendingEdit pendingEdit : pendingEdits) {	
			View pendingRow = getLayoutInflater().inflate(R.layout.pending_edit_row, null);
			String value = pendingEdit.getValue();
			String username = pendingEdit.getUsername();
			String date = "";
			try {
				date = pendingEdit.getSubmittedTime().toLocaleString();
			} catch (Exception e) {
				e.printStackTrace();
			}
			int id = pendingEdit.getId();
						
			((TextView)pendingRow.findViewById(R.id.value)).setText(value);
			((TextView)pendingRow.findViewById(R.id.date)).setText(date);
			((TextView)pendingRow.findViewById(R.id.user_name)).setText(username);
			
			CheckBox cb = (CheckBox)pendingRow.findViewById(R.id.checkBox);
			allPending.add(cb);
			cb.setOnClickListener(checkBoxClickListener);
			container.addView(pendingRow);
		}
	}
	
	private void renderTitle() {
		((TextView)findViewById(R.id.pending_edit_label)).setText(label);
	}
	
	
	public void render() throws JSONException {
		renderTitle();
		renderCurrentValue();
		renderPendingValues();
	}

	
	public OnClickListener checkBoxClickListener  = new OnClickListener() {

		@Override
		public void onClick(View v) {
			selectedValue = (CheckBox)v;
			currentValue.setChecked(currentValue == v);
			for (CheckBox cc : allPending) {
				cc.setChecked(v == cc);
			}
		}					
	};
	
	
	public void handleSaveClick(View view) {
		if (selectedValue == null) {
			setResult(Field.PENDING_ITEM_UPDATE_CANCELLED);
			finish();
		} else if (selectedValue == currentValue) {
			try {
				rejectAll();
			} catch (JSONException e) {
				e.printStackTrace();
				Toast.makeText(this, "There was a problem saving the pending edits", Toast.LENGTH_LONG).show();
			}
		} else {
			accept(selectedValue);
		}
	}
	
	private void accept(CheckBox c) {
		RequestGenerator rc = new RequestGenerator();
		int idToAccept = (Integer) c.getTag();
		if (c.getTag() != null) {
			try {
				rc.acceptPendingEdit(PendingItemDisplay.this, idToAccept, handleAcceptedPendingEdit);
			} catch (Exception e) {
				Toast.makeText(PendingItemDisplay.this, "Error accepting pending edit", Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			} 
		}
	}
	
	private void rejectAll() throws JSONException {
		PendingEditDescription ped = plot.getPendingEditForKey(key);
		
		int firstIdToReject = ped.getPendingEdits().get(0).getId(); 
		RequestGenerator rc = new RequestGenerator();
		try {
			rc.rejectPendingEdit(PendingItemDisplay.this, firstIdToReject, createRejectionResponseHandlder(key));
		} catch (UnsupportedEncodingException e) {
			Toast.makeText(PendingItemDisplay.this, "Error rejecting all pending edits", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}
		
	public  JsonHttpResponseHandler createRejectionResponseHandlder(final String key) {
		return new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONObject plotData) {
				try {
					processNextId(plotData);
				} catch (JSONException e) {
					e.printStackTrace();
					doError();
				}
			}
				
			protected void processNextId(JSONObject plotData) throws JSONException {
				Plot plot = new Plot();
				plot.setData(plotData);
				PendingEditDescription ped = plot.getPendingEditForKey(key);
				if (ped == null) {
					Intent intent = new Intent();
					intent.putExtra("plot", plotData.toString());
					setResult(Field.PENDING_ITEM_UPDATE_OK, intent);
					finish();
				} else {
					int nextIdToReject = ped.getPendingEdits().get(0).getId();
					RequestGenerator rc = new RequestGenerator();
					try {
						rc.rejectPendingEdit(PendingItemDisplay.this, nextIdToReject, createRejectionResponseHandlder(key));
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
						doError();
					} 
				}
			}
			protected void handleFailureMessage(Throwable arg0, String arg1) {
				Log.e("PENDING", arg0.toString());
				Log.e("PENDING", arg1);
				doError();
			}
					
			protected void doError() {
				Toast.makeText(PendingItemDisplay.this, "Error with pending edits", Toast.LENGTH_SHORT).show();
			}
		};
	}
	
	private JsonHttpResponseHandler handleAcceptedPendingEdit = new JsonHttpResponseHandler() {
		public void onSuccess(JSONObject plotData) {
			Intent intent = new Intent();
			intent.putExtra("plot", plotData.toString());
			setResult(Field.PENDING_ITEM_UPDATE_OK, intent);
			finish();
		};
		protected void handleFailureMessage(Throwable arg0, String arg1) {
			Log.e("PENDING", arg0.toString());
			Log.e("PENDING", arg1);
			Toast.makeText(PendingItemDisplay.this, "Error with pending edits", Toast.LENGTH_SHORT).show();
		};		
	};
	
}
