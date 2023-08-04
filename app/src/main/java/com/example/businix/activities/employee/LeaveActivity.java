package com.example.businix.activities.employee;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.businix.R;
import com.example.businix.adapters.DateLeaveAdapter;
import com.example.businix.controllers.EmployeeController;
import com.example.businix.controllers.LeaveRequestController;
import com.example.businix.controllers.LeaveRequestDetailController;
import com.example.businix.controllers.NotificationController;
import com.example.businix.models.Employee;
import com.example.businix.models.LeaveRequest;
import com.example.businix.models.LeaveRequestDetail;
import com.example.businix.models.Notification;
import com.example.businix.models.UserRole;
import com.example.businix.ui.ActionBar;
import com.example.businix.ui.CustomDialog;
import com.example.businix.utils.DateUtils;
import com.example.businix.utils.LoadingOverlayUtils;
import com.example.businix.utils.LoginManager;
import com.example.businix.utils.OnSpinnerChangeListener;
import com.google.firebase.firestore.DocumentReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class LeaveActivity extends ActionBar implements OnSpinnerChangeListener {
    private LinearLayout showDetail;
    private ListView listViewDetail;
    private TextView inputStartDate, inputEndDate;
    private TextView btnAllDay, btnMorningShift, btnAfternoonShift;
    private LinearLayout btnSend;
    private EditText etContent;
    private DateLeaveAdapter dateAdapter;

    private LeaveRequestController leaveRequestController;
    private LeaveRequestDetailController leaveRequestDetailController;
    private EmployeeController employeeController;
    private LoginManager loginManager;

    private Employee employee;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportMyActionBar("Đơn xin nghỉ", true, false);
        showDetail = (LinearLayout) findViewById(R.id.show_detail);
        listViewDetail = findViewById(R.id.list_view_detail);

        inputStartDate = findViewById(R.id.input_start_date);
        inputEndDate = findViewById(R.id.input_end_date);
        btnAllDay = findViewById(R.id.btn_all_day);
        btnMorningShift = findViewById(R.id.btn_morning_shift);
        btnAfternoonShift = findViewById(R.id.btn_afternoon_shift);
        btnSend = findViewById(R.id.btn_send);
        etContent = findViewById(R.id.et_content);


        currentSelectedItem = btnAllDay;

        leaveRequestController = new LeaveRequestController();
        leaveRequestDetailController = new LeaveRequestDetailController();
        employeeController = new EmployeeController();

        loginManager = new LoginManager(this);

        employeeController.getEmployeeById(loginManager.getLoggedInUserId(), task -> {
            if (task.isSuccessful()) {
                employee = task.getResult();
            }
        });

        // Khi bấm vào EditText Ngày đầu
        inputStartDate.setOnClickListener(v -> {
            showDatePickerDialog(inputStartDate);
        });

        // Khi bấm vào EditText Ngày cuối
        inputEndDate.setOnClickListener(v -> {
            showDatePickerDialog(inputEndDate);
        });

        showDetail.setOnClickListener(v -> {
            if (listViewDetail.getVisibility() == View.GONE) {
                listViewDetail.setVisibility(View.VISIBLE);
                if (listViewDetail.getAdapter() == null)
                    return;
                listViewDetail.post(() -> {
                    setHeightListView();
                });
            } else
                listViewDetail.setVisibility(View.GONE);
        });

        btnAllDay.setOnClickListener(v -> {
            selectShiftAllDay();
        });

        btnMorningShift.setOnClickListener(v -> {
            selectMorningShift();
        });

        btnAfternoonShift.setOnClickListener(v -> {
            selectAfternoonShift();
        });

        btnSend.setOnClickListener(v -> {
            String reason = etContent.getText().toString();
            String startDate = inputStartDate.getText().toString();
            String endDate = inputEndDate.getText().toString();
            CustomDialog customDialog = new CustomDialog(this, R.layout.custom_dialog_2);

            if (reason.strip().isBlank() || startDate.isBlank() || endDate.isBlank()) {
                customDialog.show();
                customDialog.setQuestion("WARNING");
                customDialog.setMessage("Vui lòng nhập đầy đủ thông tin");
            } else {
                customDialog.show();
                customDialog.setQuestion("Xác nhận gửi");
                customDialog.setMessage("Bạn có chắc chắn muốn gửi yêu cầu xin nghỉ này không");
                customDialog.setOnContinueClickListener((dialog, which) -> {
                    dialog.dismiss();
                    try {
                        Date start = DateUtils.changeStringToDate(startDate);
                        Calendar nowCal = Calendar.getInstance();
                        nowCal.set(Calendar.HOUR_OF_DAY, 0);
                        nowCal.set(Calendar.MINUTE, 0);
                        nowCal.set(Calendar.SECOND, 0);
                        nowCal.set(Calendar.MILLISECOND, 0);
                        nowCal.add(Calendar.DAY_OF_MONTH, 1);
                        if (!start.after(nowCal.getTime())) {
                            CustomDialog errorDialog = new CustomDialog(this, R.layout.custom_dialog_2);
                            errorDialog.show();
                            errorDialog.setQuestion("Thông tin không hợp lệ");
                            errorDialog.setMessage("Ngày bắt đầu nghỉ phải cách thời gian hiện tại 2 ngày");
                            return;
                        }
                        Date end = DateUtils.changeStringToDate(endDate);
                        if (!end.after(start)) {
                            CustomDialog errorDialog = new CustomDialog(this, R.layout.custom_dialog_2);
                            errorDialog.show();
                            errorDialog.setQuestion("Thông tin không hợp lệ");
                            errorDialog.setMessage("Ngày bắt đầu nghỉ không thể sau ngày kết thúc nghỉ");
                            return;
                        }
                        String employeeId = loginManager.getLoggedInUserId();
                        DocumentReference empRef = employeeController.getEmployeeRef(employeeId);
                        leaveRequestController.getLeaveRequestOverlapping(start, end, empRef, task -> {
                            if (task.isSuccessful()) {
                                if (task.getResult().size() > 0) {
                                    CustomDialog errorDialog = new CustomDialog(this, R.layout.custom_dialog_2);
                                    errorDialog.show();
                                    errorDialog.setQuestion("Thông tin không hợp lệ");
                                    errorDialog.setMessage("Đã có đơn nghỉ trùng thời gian với đơn hiện tại, vui lòng kiểm tra lại!");
                                }
                                else {
                                    LoadingOverlayUtils.showLoadingOverlay(this);
                                    LeaveRequest leaveRequest = new LeaveRequest();
                                    leaveRequest.setFromDate(start);
                                    leaveRequest.setToDate(end);
                                    leaveRequest.setEmployee(empRef);
                                    leaveRequest.setReason(reason);
                                    leaveRequestController.addLeaveRequest(leaveRequest, task1 -> {
                                        if (task1.isSuccessful()) {
                                            String leaveRequestId = task1.getResult();
                                            listViewDetail.setVisibility(View.VISIBLE);
                                            listViewDetail.post(() -> {
                                                setHeightListView();
                                                List<LeaveRequestDetail> detailList = new ArrayList<>();
                                                int count = listViewDetail.getChildCount();
                                                for (int i = 0; i < count; i++) {
                                                    View itemView = listViewDetail.getChildAt(i);
                                                    TextView tvDate = itemView.findViewById(R.id.text_date);
                                                    Spinner spinner = itemView.findViewById(R.id.spinner_shift);
                                                    String selection = spinner.getSelectedItem().toString();
                                                    String itemDate = tvDate.getText().toString();
                                                    LeaveRequestDetail leaveRequestDetail = new LeaveRequestDetail();
                                                    try {
                                                        leaveRequestDetail.setDate(DateUtils.changeStringToDate(itemDate));
                                                        leaveRequestDetail.setShift(selection);
                                                        leaveRequestDetail.setLeaveRequest(leaveRequestController.getLeaveRequestRef(leaveRequestId));
                                                        detailList.add(leaveRequestDetail);
                                                    } catch (ParseException e) {
                                                        throw new RuntimeException(e);
                                                    }
                                                }
                                                leaveRequestDetailController.addLeaveRequestDetails(detailList, task2 -> {
                                                    CustomDialog subDialog = new CustomDialog(this, R.layout.custom_dialog_2);
                                                    if (task2.isSuccessful()) {
                                                        subDialog.show();
                                                        subDialog.setQuestion("Thông báo");
                                                        subDialog.setMessage("Đã gửi thành công đơn xin nghỉ");
                                                        LoadingOverlayUtils.hideLoadingOverlay();
                                                        Notification notification = new Notification();
                                                        notification.setRead(false);
                                                        notification.setSender(employeeController.getEmployeeRef(employeeId));
                                                        notification.setTitle("Đơn xin nghỉ");
                                                        String msg = "Một đơn xin nghỉ đến từ nhân viên";
                                                        if (employee != null) {
                                                            msg += " " + employee.getFullName();
                                                        }
                                                        notification.setMessage(msg);
                                                        employeeController.getEmployeeListByRole(UserRole.ADMIN, task3 -> {
                                                            if (task3.isSuccessful()) {
                                                                notification.setReceivers(task3.getResult());
                                                                (new NotificationController()).addNotification(notification, task4 -> {
                                                                    if (task4.isSuccessful()) {

                                                                    }
                                                                });
                                                            }
                                                        });
                                                    } else {
                                                        subDialog.show();
                                                        subDialog.setQuestion("Thông báo");
                                                        subDialog.setMessage("Đã có lỗi xảy ra");
                                                        LoadingOverlayUtils.hideLoadingOverlay();
                                                    }
                                                });
                                            });

                                        }
                                    });
                                }
                            } else {
                                CustomDialog errorDialog = new CustomDialog(this, R.layout.custom_dialog_2);
                                errorDialog.show();
                                errorDialog.setQuestion("Đã có lỗi xảy ra");
                                errorDialog.setMessage("Vui lòng thử lại sau!");
                                finish();
                            }
                        });

                    } catch (ParseException e) {
                        LoadingOverlayUtils.hideLoadingOverlay();
                        throw new RuntimeException(e);
                    }

                });
            }
        });
    }

    private void showDatePickerDialog(TextView dateText) {
        // Lấy ngày hiện tại để đặt làm giá trị mặc ring cho DatePickerDialog
        int year;
        int month;
        int dayOfMonth;
        if (dateText.getText().toString().isEmpty()) {
            Calendar calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        } else {
            String[] endDateArr = dateText.getText().toString().split("/");
            year = Integer.parseInt(endDateArr[2]);
            month = Integer.parseInt(endDateArr[1]) - 1;
            dayOfMonth = Integer.parseInt(endDateArr[0]);
        }


        // Tạo DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, yearSelected, monthOfYear, dayOfMonthSelected) -> {
                    // Lắng nghe sự kiện chọn ngày và cập nhật giá trị cho EditText tương ứng
                    String selectedDate = dayOfMonthSelected + "/" + (monthOfYear + 1) + "/" + yearSelected;
                    dateText.setText(selectedDate);
                    generateDateList();
                },
                year,
                month,
                dayOfMonth
        );

        // Hiển thị DatePickerDialog
        datePickerDialog.show();
    }

    private void generateDateList() {
        String startDateStr = inputStartDate.getText().toString();
        String endDateStr = inputEndDate.getText().toString();

        List<String> dateList = generateDateRange(startDateStr, endDateStr);

        dateAdapter = new DateLeaveAdapter(this, dateList);
        dateAdapter.setSpinnerChangeListener(this::onSpinnerChanged);
        listViewDetail.setAdapter(dateAdapter);
        setHeightListView();
    }

    private List<String> generateDateRange(String startDateStr, String endDateStr) {
        List<String> dateList = new ArrayList<>();

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date startDate = sdf.parse(startDateStr);
            Date endDate = sdf.parse(endDateStr);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startDate);

            while (!calendar.getTime().after(endDate)) {
                dateList.add(sdf.format(calendar.getTime()));
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dateList;
    }

    private void setHeightListView() {
        View itemLayout = getLayoutInflater().inflate(R.layout.list_item_leave_dates, null);
        itemLayout.measure(
                View.MeasureSpec.makeMeasureSpec(listViewDetail.getWidth(), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        );
        int itemHeight = itemLayout.getMeasuredHeight();
        listViewDetail.post(() -> {
            int totalItems = dateAdapter.getCount();
            int height = totalItems * (itemHeight + 3);
            ViewGroup.LayoutParams params = listViewDetail.getLayoutParams();
            params.height = height;
            listViewDetail.setLayoutParams(params);
        });
    }

    private TextView currentSelectedItem;

    private void selectShiftAllDay() {
        changeStatusBtn(btnAllDay);


        currentSelectedItem = btnAllDay;
        int count = listViewDetail.getChildCount();
        for (int i = 0; i < count; i++) {
            View itemView = listViewDetail.getChildAt(i);
            Spinner spinner = itemView.findViewById(R.id.spinner_shift);
            // Thực hiện việc chọn "Cả ngày" trong Spinner của từng item trong ListView
            spinner.setSelection(0, true);
        }
    }

    private void changeStatusBtn(TextView btn) {
        if (currentSelectedItem != null) {
            currentSelectedItem.setBackgroundTintList(ColorStateList.valueOf(ResourcesCompat.getColor(getResources(), R.color.white, null)));
            currentSelectedItem.setTextColor(ResourcesCompat.getColor(getResources(), R.color.black, null));
        }


        btn.setBackgroundTintList(ColorStateList.valueOf(ResourcesCompat.getColor(getResources(), R.color.light_purple, null)));
        btn.setTextColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
    }

    private void changeStatusBtn() {
        if (currentSelectedItem != null) {
            currentSelectedItem.setBackgroundTintList(ColorStateList.valueOf(ResourcesCompat.getColor(getResources(), R.color.white, null)));
            currentSelectedItem.setTextColor(ResourcesCompat.getColor(getResources(), R.color.black, null));
        }
    }


    private void selectMorningShift() {
        changeStatusBtn(btnMorningShift);

        currentSelectedItem = btnMorningShift;
        int count = listViewDetail.getChildCount();
        for (int i = 0; i < count; i++) {
            View itemView = listViewDetail.getChildAt(i);
            Spinner spinner = itemView.findViewById(R.id.spinner_shift);
            // Thực hiện việc chọn "Cả ngày" trong Spinner của từng item trong ListView
            spinner.setSelection(1, true);
        }
    }

    private void selectAfternoonShift() {
        changeStatusBtn(btnAfternoonShift);


        currentSelectedItem = btnAfternoonShift;
        int count = listViewDetail.getChildCount();
        for (int i = 0; i < count; i++) {
            View itemView = listViewDetail.getChildAt(i);
            Spinner spinner = itemView.findViewById(R.id.spinner_shift);
            // Thực hiện việc chọn "Cả ngày" trong Spinner của từng item trong ListView
            spinner.setSelection(2, true);
        }
    }

    @Override
    public void onSpinnerChanged(int position, String selectedItem) {
        switch (position) {
            case 0:
                if (currentSelectedItem.getId() == btnAllDay.getId())
                    return;
                else checkBtnShift();
                break;
            case 1:
                if (currentSelectedItem.getId() == btnMorningShift.getId())
                    return;
                else checkBtnShift();
                break;
            case 2:
                if (currentSelectedItem.getId() == btnAfternoonShift.getId())
                    return;
                else checkBtnShift();
                break;
            default:
                break;
        }

    }

    private void checkBtnShift() {
        int count = listViewDetail.getChildCount();
        boolean isSame = true;
        int selection = -1;
        for (int i = 0; i < count; i++) {
            View itemView = listViewDetail.getChildAt(i);
            Spinner spinner = itemView.findViewById(R.id.spinner_shift);
            if (selection == -1)
                selection = spinner.getSelectedItemPosition();
            else if (spinner.getSelectedItemPosition() != selection) {
                isSame = false;
                break;
            }
        }
        if (isSame) {
            switch (selection) {
                case 0:
                    changeStatusBtn(btnAllDay);
                    currentSelectedItem = btnAllDay;
                    break;
                case 1:
                    changeStatusBtn(btnMorningShift);
                    currentSelectedItem = btnMorningShift;
                    break;
                case 2:
                    changeStatusBtn(btnAfternoonShift);
                    currentSelectedItem = btnAfternoonShift;
            }
        } else {
            changeStatusBtn();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.history_item, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.opt_history) {
            Intent intent = new Intent(LeaveActivity.this, LeaveRequestHistoryActivity.class);
            startActivity(intent);
        }
        if (itemId == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}