package org.azavea.otm.data;

import org.json.JSONException;
import org.json.JSONObject;

public class Tree extends Model {
	public Tree() {
		data = new JSONObject();
	}
	
	public int getId() throws JSONException {
		return data.getInt("id");
	}

	public void setId(int id) throws JSONException {
		data.put("id", id);
	}

	public long getDbh() throws JSONException {
		return getLongOrDefault("dbh", 0l);
	}

	public void setDbh(double dbh) throws JSONException {
		data.put("dbh", dbh);
	}

	public String getDateRemoved() throws JSONException {
		return data.getString("date_removed");
	}

	public void setDateRemoved(String dateRemoved) throws JSONException {
		data.put("date_removed", dateRemoved);
	}

	public String getSpeciesName() throws JSONException {
		return data.getString("species_name");
	}

	public void setSpeciesName(String speciesName) throws JSONException {
		data.put("species_name", speciesName);
	}

	public String getTreeOwner() throws JSONException {
		return data.getString("tree_owner");
	}

	public void setTreeOwner(String treeOwner) throws JSONException {
		data.put("tree_owner", treeOwner);
	}

	public String getDatePlanted() throws JSONException {
		return data.getString("date_planted");
	}

	public void setDatePlanted(String datePlanted) throws JSONException {
		data.put("date_planted", datePlanted);
	}

	public String getCondition() throws JSONException {
		return data.getString("condition");
	}

	public void setCondition(String condition) throws JSONException {
		data.put("condition", condition);
	}

	public boolean isReadOnly() throws JSONException {
		return data.getBoolean("readonly");
	}

	public void setReadOnly(boolean readOnly) throws JSONException{
		data.put("readonly", readOnly);
	}

	public String getSpecies() throws JSONException{
		return data.getString("species");
	}

	public void setSpecies(String species) throws JSONException {
		data.put("species", species);
	}

	public String getSpeciesOther1() throws JSONException {
		return data.getString("species_other1");
	}

	public void setSpeciesOther1(String speciesOther1) throws JSONException {
		data.put("species_other1", speciesOther1);
	}

	public String getSpeciesOther2() throws JSONException {
		return data.getString("species_other2");
	}

	public void setSpeciesOther2(String speciesOther2) throws JSONException {
		data.put("species_other2", speciesOther2);
	}

	public String getLastUpdated() throws JSONException {
		return data.getString("last_updated");
	}

	public void setLastUpdated(String lastUpdated) throws JSONException {
		data.put("last_updated", lastUpdated);
	}

	public String getLastUpdatedBy() throws JSONException {
		return data.getString("last_updated_by");
	}

	public void setLastUpdatedBy(String lastUpdatedBy) throws JSONException {
		data.put("last_updated_by", lastUpdatedBy);
	}

	public boolean isPresent() throws JSONException {
		return data.getBoolean("present");
	}

	public void setPresent(boolean present) throws JSONException {
		data.put("present", present);
	}

	public String getSponsor() throws JSONException {
		return data.getString("sponsor");
	}

	public void setSponsor(String sponsor) throws JSONException {
		data.put("sponsor", sponsor);
	}

	public String getCanopyCondition() throws JSONException {
		return data.getString("canopy_condition");
	}

	public void setCanopyCondition(String canopyCondition) throws JSONException {
		data.put("canopy_condition", canopyCondition);
	}

	public String getScientificName() throws JSONException {
		return data.getString("sci_name");
	}

	public void setScientificName(String scientificName) throws JSONException {
		data.put("sci_name", scientificName);
	}

	public String getStewardName() throws JSONException {
		return data.getString("steward_name");
	}

	public void setStewardName(String stewardName) throws JSONException {
		data.put("steward_name", stewardName);
	}
}
