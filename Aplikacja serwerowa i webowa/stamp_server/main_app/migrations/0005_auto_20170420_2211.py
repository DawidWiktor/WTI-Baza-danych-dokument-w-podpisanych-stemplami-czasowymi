# -*- coding: utf-8 -*-
# Generated by Django 1.10.6 on 2017-04-20 20:11
from __future__ import unicode_literals

from django.db import migrations


class Migration(migrations.Migration):

    dependencies = [
        ('main_app', '0004_auto_20170419_1037'),
    ]

    operations = [
        migrations.RenameField(
            model_name='documents',
            old_name='date',
            new_name='timestamp',
        ),
    ]
