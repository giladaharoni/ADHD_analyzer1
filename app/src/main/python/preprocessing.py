import pandas as pd
import numpy as np


def preprocessing():
    # Read in sensor log data as a pandas dataframe
    df = pd.read_csv('sensor_logs.csv')

    # Define threshold values for major and minor sensor changes
    major_threshold = 1.0
    minor_threshold = 0.1

    # Compute the differences between consecutive sensor readings for each sensor type
    df_diff = df.diff()

    # Compute the norm of the accelerometer, gyro, and magnetometer differences
    df_diff_norm = np.linalg.norm(df_diff[['accel_x', 'accel_y', 'accel_z',
                                           'gyro_x', 'gyro_y', 'gyro_z',
                                           'mag_x', 'mag_y', 'mag_z']], axis=1)

    # Compute the norm of the GPS differences
    df_gps_norm = np.linalg.norm(df_diff[['lat', 'lon']], axis=1)

    # Compute a boolean mask for major sensor changes and minor or no GPS changes
    mask = (df_diff_norm > major_threshold) & (df_gps_norm <= minor_threshold)

    # Compute the size of each group of True values in the mask array
    group_sizes = [sum(1 for _ in group) for key, group in itertools.groupby(mask) if key]

    # Compute the average size of True groups
    avg_group_size = np.mean(group_sizes)

    print(f"Average size of groups of True values in the mask array: {avg_group_size:.2f}")
