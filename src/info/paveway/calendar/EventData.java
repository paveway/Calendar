package info.paveway.calendar;

import info.paveway.util.StringUtil;

public class EventData {

    private String mId;

    private String mSummary;

    private String mLocation;

    private String mStartDateTime;

    private String mEndDateTime;

    public void setId(String id) {
        mId = id;
    }

    public String getId() {
        return mId;
    }

    public void setSummary(String summary) {
        mSummary = summary;
    }

    public String getSummary() {
        return mSummary;
    }

    public void setLocation(String location) {
        mLocation = location;
    }

    public String getLocation() {
        return mLocation;
    }

    public void setStartDateTime(String startDateTime) {
        mStartDateTime = startDateTime;
    }

    public String getStartDateTime() {
        return mStartDateTime;
    }

    public int getStartYear() {
        if (StringUtil.isNotNullOrEmpty(mStartDateTime)) {
            return Integer.parseInt(mStartDateTime.substring(0, 4));

        } else {
            return 0;
        }
    }

    public int getStartMonth() {
        if (StringUtil.isNotNullOrEmpty(mStartDateTime)) {
            return Integer.parseInt(mStartDateTime.substring(5, 7));

        } else {
            return 0;
        }
    }

    public int getStartDate() {
        if (StringUtil.isNotNullOrEmpty(mStartDateTime)) {
            return Integer.parseInt(mStartDateTime.substring(8, 10));

        } else {
            return 0;
        }
    }

    public int getStartHour() {
        if (StringUtil.isNotNullOrEmpty(mStartDateTime)) {
            return Integer.parseInt(mStartDateTime.substring(11, 13));

        } else {
            return 0;
        }
    }

    public int getStartMinute() {
        if (StringUtil.isNotNullOrEmpty(mStartDateTime)) {
            return Integer.parseInt(mStartDateTime.substring(14, 16));

        } else {
            return 0;
        }
    }

    public void setEndDateTime(String endDateTime) {
        mEndDateTime = endDateTime;
    }

    public String getEndDateTime() {
        return mEndDateTime;
    }
}
