package com.ixxc.uiot;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.ixxc.uiot.Adapter.ConfigurationAdapter;
import com.ixxc.uiot.Adapter.ParamsAdapter;
import com.ixxc.uiot.Model.Attribute;
import com.ixxc.uiot.Model.Device;
import com.ixxc.uiot.Model.MetaItem;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EditAttributeActivity extends AppCompatActivity {
    Toolbar toolbar;
    ActionBar actionBar;
    TextView tv_name, tv_type;
    TextInputEditText  et_value;
    Button btn_add_config;
    ImageButton ibtn_copy, ibtn_copy_1;
    LinearLayout linear_config_items;

    List<MetaItem> selectedMeta = new ArrayList<>();
    List<MetaItem> metaModels;
    Attribute attribute;
    JsonObject attributeMeta;
    int currentColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_attribute);

        InitVars();
        InitViews();
        InitEvents();

        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(Utils.formatString(attribute.getName()));

        tv_name.setText(attribute.getName());
        tv_type.setText(attribute.getType());
        et_value.setInputType(Utils.getInputType(attribute.getType()));
        et_value.setText(attribute.getValueString());

        ibtn_copy.setImageTintList(ColorStateList.valueOf(currentColor));
        ibtn_copy_1.setImageTintList(ColorStateList.valueOf(currentColor));
        btn_add_config.setBackgroundTintList(ColorStateList.valueOf(currentColor));

        loadMetaItems();
    }

    private void InitEvents() {
        et_value.setOnFocusChangeListener((view, focused) -> {
            if (!focused) {
                attribute.setValue(new JsonPrimitive(String.valueOf(et_value.getText())));
            }
        });

        btn_add_config.setOnClickListener(view -> {
            Dialog dlg = new Dialog(this);
            dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dlg.setContentView(R.layout.add_configuration_items);

            Window window = dlg.getWindow();
            if (window == null) return;
            window.setBackgroundDrawableResource(R.drawable.bg_add_config_menu);
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

            TextView tv_title = dlg.findViewById(R.id.tv_title);
            tv_title.setBackgroundColor(currentColor);

            Button btn_cancel = dlg.findViewById(R.id.btn_cancel);
            btn_cancel.setTextColor(currentColor);
            btn_cancel.setOnClickListener(view1 -> {
                selectedMeta.clear();
                dlg.dismiss();
            });

            Button btn_add = dlg.findViewById(R.id.btn_save);
            btn_add.setTextColor(currentColor);
            btn_add.setOnClickListener(view1 -> {
                linear_config_items.removeAllViews();

                attributeMeta.keySet().clear();
                for (MetaItem item : selectedMeta) {
                    String value = attribute.getMetaValue(item.getName());
                    addMetaItem(item, value);
                }

                selectedMeta.clear();
                dlg.dismiss();
            });

            ConfigurationAdapter configAdapter = new ConfigurationAdapter(this, attributeMeta, currentColor);
            configAdapter.setListener((checked, name) -> {
                if (checked) {
                    selectedMeta.add(metaModels.stream().filter(item -> item.getName().equals(name)).findFirst().orElse(null));
                } else {
                    selectedMeta.removeIf(item -> item.getName().equals(name));
                }
            });

            RecyclerView rv_config_item = dlg.findViewById(R.id.rv_item);
            rv_config_item.setLayoutManager(new LinearLayoutManager(this));
            rv_config_item.setHasFixedSize(true);
            rv_config_item.setAdapter(configAdapter);
            dlg.show();
        });

        // TODO: Add ibtn_copy click event
    }

    private void InitVars() {
        currentColor = getIntent().getIntExtra("COLOR", R.color.bg);
        String jsonString = getIntent().getStringExtra("ATTRIBUTE");

        // Don't move this line to other place
        toolbar = findViewById(R.id.action_bar);
        toolbar.setBackgroundColor(currentColor);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();

        attribute = new Gson().fromJson(jsonString, Attribute.class);
        attributeMeta = attribute.getMeta() == null ? new JsonObject() : attribute.getMeta();
        metaModels = MetaItem.getMetaItemList();
    }

    private void InitViews() {
        tv_name = findViewById(R.id.tv_name);
        tv_type = findViewById(R.id.tv_type);
        et_value = findViewById(R.id.et_value);
        btn_add_config = findViewById(R.id.btn_add_configuration_items);
        linear_config_items = findViewById(R.id.linear_config_items);
        ibtn_copy = findViewById(R.id.ib_copy);
        ibtn_copy_1 = findViewById(R.id.ib_copy_1);
    }

    private void loadMetaItems() {
        if (attributeMeta == null) return;

        for (String key : attributeMeta.keySet()) {
            String value = attribute.getMetaValue(key);
            metaModels.stream()
                    .filter(metaItem -> metaItem.getName().equals(key))
                    .findFirst().ifPresent(item -> addMetaItem(item, value));
        }
    }

    private void addMetaItem(MetaItem item, String value) {
        switch (item.getType()) {
            case "boolean":
                attributeMeta.addProperty(item.getName(), !TextUtils.isEmpty(value) && Boolean.parseBoolean(value));
                break;
            case "text":
                attributeMeta.addProperty(item.getName(), value);
                break;
            default:
                attributeMeta.add(item.getName(), TextUtils.isEmpty(value) ? new JsonObject() : JsonParser.parseString(value));
                break;
        }

        View view = createConfigView(item.getName(), item.getType(), value);
        if (view instanceof CheckBox) linear_config_items.addView(view, 0);
        else linear_config_items.addView(view, linear_config_items.getChildCount());
    }

    private View createConfigView(String name, String type, String value) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 16, 0, 0);

        switch (type) {
            case "boolean":
                CheckBox cb = new CheckBox(this);
                cb.setButtonTintList(ColorStateList.valueOf(currentColor));
                cb.setText(Utils.formatString(name));
                cb.setChecked(value.equals("true"));
                cb.setLayoutParams(params);
                cb.setTag(name);
                cb.setOnCheckedChangeListener((compoundButton, checked) -> attributeMeta.addProperty(name, checked));

                return cb;
            case "valueFormat":
            case "text":
            case "positiveInteger":
            case "attributeLink[]":
            case "forecastConfiguration":
                return createTextInputView(name, type, value);
            case "agentLink":
                return createAgentLinkView(value);
            case "valueConstraint[]":
            case "text[]":
            default:
                return null;
        }
    }

    private View createAgentParamView(String param, JsonObject agentObject) {
        String meta = "agentLink";

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 16, 0, 0);

        TextInputLayout til = new TextInputLayout(this);
        til.setTag(param);
        til.setHint(Utils.formatString(param));
        til.setLayoutParams(layoutParams);

        TextInputEditText et = new TextInputEditText(til.getContext());
        et.setTag("et_" + param);
        et.setInputType(Utils.getInputType(param));

        switch (param) {
            case "valueFilters":
                et.setText(agentObject.has(param) ? agentObject.get(param).getAsJsonArray().get(0).getAsJsonObject().get("path").getAsString() : "");
                et.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        // Create new filter object
                        JsonArray filters = new JsonArray();
                        JsonObject filter = new JsonObject();
                        filter.addProperty("type", "jsonPath");
                        filter.addProperty("path", String.valueOf(editable));
                        filters.add(filter);

                        agentObject.add(param, filters);
                        attributeMeta.add(meta, agentObject);
                    }
                });

                til.addView(et);
                return til;
            case "path":
            case "pollingMillis":
                et.setText(agentObject.has(param) ? agentObject.get(param).getAsString() : "");
                et.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        agentObject.addProperty(param, String.valueOf(editable));
                        attributeMeta.add(meta, agentObject);
                    }
                });

                til.addView(et);

                return til;
            default:
                return null;
        }
    }

    private View createAgentLinkView(String value) {
        String meta = "agentLink";
        // Get agent name list and set adapter
        List<String> agentNames = Device.getDeviceList().stream()
                .filter(device -> device.type.contains("Agent"))
                .map(device -> device.name + " (" + device.id + ")").collect(Collectors.toList());
        ArrayAdapter<String> agentAdapter = new ArrayAdapter<>(this, R.layout.dropdown_item, agentNames);

        // Set params
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(4, 16, 4, 4);

        // Get agent link object
        JsonElement element;
        JsonObject agentLinkObject;
        JsonObject changedParams;
        if (!value.isEmpty() && (element = JsonParser.parseString(value)) != null && element.isJsonObject()) {
            // Attribute has already agent link
            agentLinkObject = element.getAsJsonObject();
        } else {
            // Blank agent link
            agentLinkObject = new JsonObject();
            agentLinkObject.addProperty("id", "");
        }

        changedParams = agentLinkObject.deepCopy();

        // Create card view
        MaterialCardView cardView = (MaterialCardView) View.inflate(this, R.layout.agent_link_layout, null);
        cardView.setLayoutParams(params);

        LinearLayout linear = cardView.findViewById(R.id.linear_agent_link);

        // AutoCompleteTextView for agent selection
        AutoCompleteTextView act = cardView.findViewById(R.id.act_agent_link);
        act.setAdapter(agentAdapter);
        act.setOnItemClickListener((adapterView, view, i, l) -> {
            act.setSelection(0);
            String id = agentNames.get(i).substring(agentNames.get(i).indexOf("(") + 1, agentNames.get(i).indexOf(")"));
            Device agent = Device.getDeviceById(id);
            if (agent != null) {
                agentLinkObject.addProperty("id", agent.id);
                agentLinkObject.addProperty("type", agent.type);
                attributeMeta.add(meta, agentLinkObject);
            }
        });

        // Set value
        Device device = Device.getDeviceById(agentLinkObject.get("id").getAsString());
        if (device != null) {
            act.setText(String.join("", device.name, " (", device.id, ")" ));
        }

        for (String param : agentLinkObject.keySet()) {
            View view = createAgentParamView(param, agentLinkObject);
            if (view != null) linear.addView(view);
        }

        // Set add params button event
        Button btn_add_params = cardView.findViewById(R.id.btn_add_params);
        btn_add_params.setTextColor(currentColor);
        btn_add_params.setOnClickListener(view -> {
            // Create pop up dialog
            Dialog dlg = new Dialog(EditAttributeActivity.this);
            dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dlg.setContentView(R.layout.add_configuration_items);

            Window window = dlg.getWindow();
            if (window == null) return;
            window.setBackgroundDrawableResource(R.drawable.bg_add_config_menu);
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

            TextView  tv_title = dlg.findViewById(R.id.tv_title);
            tv_title.setBackgroundColor(currentColor);

            // Set cancel button event
            Button btn_cancel = dlg.findViewById(R.id.btn_cancel);
            btn_cancel.setTextColor(currentColor);
            btn_cancel.setOnClickListener(v -> {
                linear.removeAllViews();
                for (String param : agentLinkObject.keySet()) {
                    View view1 = createAgentParamView(param, agentLinkObject);
                    if (view1 != null) linear.addView(view1);
                }
                dlg.dismiss();
            });

            // Set save button event
            Button btn_save = dlg.findViewById(R.id.btn_save);
            btn_save.setTextColor(currentColor);
            btn_save.setOnClickListener(v -> {
                agentLinkObject.keySet().clear();
                for (String key : changedParams.keySet()) {
                    agentLinkObject.addProperty(key, changedParams.get(key).getAsString());
                }
                dlg.dismiss();
            });

            ParamsAdapter paramsAdapter = new ParamsAdapter(EditAttributeActivity.this, agentLinkObject, currentColor);
            paramsAdapter.setListener((checked, param) -> {
                // If checked, add item to changedParams then add view. Else remove item from changedParams then remove view
                if (checked) {
                    changedParams.addProperty(param, "");

                    // Create view and set edittext text change listener
                    View view1 = createAgentParamView(param, agentLinkObject);
                    if (view1 != null) linear.addView(view1);
                }
                else {
                    changedParams.remove(param);
                    attributeMeta.get(meta).getAsJsonObject().remove(param);
                    linear.removeView(linear.findViewWithTag(param));
                }
            });

            // Set recycler view then show dialog
            RecyclerView rv_item = dlg.findViewById(R.id.rv_item);
            rv_item.setLayoutManager(new LinearLayoutManager(EditAttributeActivity.this));
            rv_item.setHasFixedSize(true);
            rv_item.setAdapter(paramsAdapter);
            dlg.show();
        });

        return cardView;
    }

    private View createTextInputView(String name, String type, String value) {
        TextInputLayout til1 = new TextInputLayout(this);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 16, 0, 0);

        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                Utils.dpToPx(this, 120)
        );

        til1.setHint(Utils.formatString(name));
        til1.setTag(name);
        til1.setLayoutParams(params);

        TextInputEditText et1 = new TextInputEditText(til1.getContext());
        et1.setInputType(Utils.getInputType(type));
        et1.setText(value);
        et1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                attributeMeta.addProperty(name, String.valueOf(et1.getText()));
            }
        });

        if (!type.equals("text") && !type.equals("positiveInteger")) { et1.setLayoutParams(params1); }
        til1.addView(et1);
        return til1;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_attribute, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.done) {
            et_value.clearFocus();
            attribute.setMeta(attributeMeta);
            Log.d(Utils.LOG_TAG, "onOptionsItemSelected: " + attribute.toJson());
            setResult(RESULT_OK, new Intent().putExtra("ATTRIBUTE", String.valueOf(attribute.toJson())));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}