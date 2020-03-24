package ai.addx.visual.util;

import ai.addx.visual.bean.info.BatteryDTO;
import ai.addx.visual.bean.info.Incident;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class BeanUtil {
    private static BatteryDTO findLG(List<BatteryDTO> list, String target) {
        int l = 0, r = list.size() - 1;
        while (l < r) {
            int mid = l + (r - l) / 2;
            if (DateUtil.compare(list.get(mid).getTime(), target) > 0) {
                r = mid;
            } else  {
                l = mid + 1;
            }
        }
        return list.get(l);
    }
    public static List<BatteryDTO> mapBatteryInfoIncident(List<BatteryDTO> list, List<Incident> incidents) {
        for (Incident incident : incidents) {
            String time = incident.getTime();
            BatteryDTO info = BeanUtil.findLG(list, time);
            info.setIncident(incident);
        }
        return list;
    }

}
