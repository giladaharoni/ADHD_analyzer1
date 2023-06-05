import pandas as pd
import numpy as np
import json


def compress(before_comp):
    da = before_comp.copy()
    da['timestamp'] = pd.to_datetime(da['timestamp'], unit='ms')
    da.set_index('timestamp', inplace=True)
    da_resampled = da.resample('1min').agg({'stay_inplace': 'sum', 'high_adhd': 'sum'})
    da_resampled['stay_inplace'] = da_resampled['stay_inplace'] > da_resampled['stay_inplace'].sum() / 2
    da_resampled['high_adhd'] = (da_resampled['high_adhd'] > da_resampled['high_adhd'].sum() / 2) & da_resampled['stay_inplace']
    da_resampled.reset_index(inplace=True)
    da_resampled['timestamp'] = (da_resampled['timestamp'] - np.datetime64('1970-01-01T00:00:00Z')) / np.timedelta64(1, 'ms')
    da_resampled['timestamp'] = da_resampled['timestamp'].astype('int64')
    return da_resampled


def process(data):
    df = pd.DataFrame(json.loads(data))
    df = df.sort_values('timestamp')
    df_diff = df.diff(periods=5)
    timestamp = df['timestamp']
    df_diff_norm = np.linalg.norm(df_diff[['accelerometerX', 'accelerometerY', 'accelerometerZ',
                                       'gyroscopeX', 'gyroscopeY', 'gyroscopeZ',
                                       'magnetometerX', 'magnetometerY', 'magnetometerZ']], axis=1)
    df_gps_norm = np.linalg.norm(df_diff[['latitude', 'longitude']], axis=1)
    major_threshold = 7.0
    minor_threshold = 3.1
    stay_inplace = (df_gps_norm <= minor_threshold)
    high_adhd = (df_diff_norm > major_threshold) & stay_inplace
    result_table = {'timestamp': timestamp, 'stay_inplace': stay_inplace,'high_adhd':high_adhd}
    result_table = pd.DataFrame(result_table)
    return compress(result_table)