import pandas as pd
import numpy as np
def process(data):
    df = pd.DataFrame(data)
    df = df.sort_values('timestamp')
    df_diff = df.diff(periods=5)
    timestamp = df['timestamp']
    df_diff_norm = np.linalg.norm(df_diff[['accel_x', 'accel_y', 'accel_z',
                                       'gyro_x', 'gyro_y', 'gyro_z',
                                       'mag_x', 'mag_y', 'mag_z']], axis=1)
    df_gps_norm = np.linalg.norm(df_diff[['lat', 'lon']], axis=1)
    major_threshold = 7.0
    minor_threshold = 3.1
    stay_inplace = (df_gps_norm <= minor_threshold)
    high_adhd = (df_diff_norm > major_threshold) & stay_inplace
    result_table = {'timestamp': timestamp, 'stay_inplace': stay_inplace,'high_adhd':high_adhd}
    result_table = pd.DataFrame(result_table)
    return result_table