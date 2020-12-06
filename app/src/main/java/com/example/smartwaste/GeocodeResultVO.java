package com.example.smartwaste;

import java.util.ArrayList;

public class GeocodeResultVO {
    private String status;
    private String errorMessage;
    private MetaVO meta;
    private AddressVO[] addresses;

    public String getStatus() {
        return status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public MetaVO getMeta() {
        return meta;
    }

    public AddressVO[] getAddress() {
        return addresses;
    }


    public class MetaVO {
        private Integer totalCount;
        private Integer page;
        private Integer count;

        public Integer getTotalCount() {
            return totalCount;
        }

        public Integer getPage() {
            return page;
        }

        public Integer getCount() {
            return count;
        }
    }

    public class AddressVO {
        private String roadAddress;
        private String jibunAddress;
        private String englishAddress;
        private String x;
        private String y;
        private Double distance;
        private ArrayList<AddressElementVO> addressElements;

        public String getRoadAddress() {
            return roadAddress;
        }

        public String getJibunAddress() {
            return jibunAddress;
        }

        public String getEnglishAddress() {
            return englishAddress;
        }

        public String getX() {
            return x;
        }

        public String getY() {
            return y;
        }

        public Double getDistance() {
            return distance;
        }

        public ArrayList<AddressElementVO> getAddressElements() {
            return addressElements;
        }

    }

    public class AddressElementVO {
        private ArrayList<String> types;
        private String longName;
        private String shortName;
        private String code;

        public ArrayList<String> getTypes() {
            return types;
        }

        public String getLongName() {
            return longName;
        }

        public String getShortName() {
            return shortName;
        }

        public String getCode() {
            return code;
        }
    }


}
